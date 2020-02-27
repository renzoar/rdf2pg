/* 
 * Copyright 2020 Renzo Angles (http://renzoangles.com/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package maps;

import RDFSchema.PropertyClass;
import RDFSchema.ResourceClass;
import RDFSchema.Schema;
import Reader.SchemaReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

import pgraph.PropertyGraph;

/**
 *
 * @author renzo
 */
public class DirectMapping {

    private PropertyGraph pg_instance;
    private PropertyGraph pg_schema;
    private HashMap<String, String> prefixes;
    private int free_prefix = 1;

    public DirectMapping() {
        pg_instance = new PropertyGraph();
        pg_schema = new PropertyGraph();
        prefixes = new HashMap();
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://www.w3.org/2002/07/owl#", "owl");
        prefixes.put("http://www.w3.org/2001/XMLSchema#", "xsd");
    }

    public PropertyGraph getPGInstance() {
        return pg_instance;
    }

    public PropertyGraph getPGSchema() {
        return pg_schema;
    }

    private String addPrefix(String namespace) {
        if (prefixes.containsKey(namespace)) {
            return prefixes.get(namespace);
        }
        String new_prefix = "nss" + free_prefix++;
        prefixes.put(namespace, new_prefix);
        return new_prefix;
    }

    public void run(String input_instance_filename, String input_schema_filename) {
        this.runSchemaMapping(input_schema_filename);
        this.runInstanceMapping(input_instance_filename);
    }

    public PropertyGraph runSchemaMapping(String input_schema_filename) {

        SchemaReader schema_reader = new SchemaReader();
        Schema schema = schema_reader.run(input_schema_filename);
        
        //Process value properties
        Iterator<ResourceClass> classes = schema.getResourceClasses();
        while (classes.hasNext()) {
            ResourceClass rc = classes.next();
            if (rc.isDatatype()) {
                continue;
            }
            String class_name = this.addPrefix(rc.getNamespace()) + "_" + rc.getName();
            Integer node = pg_schema.addNode(rc.hashCode(), class_name);
            Iterator<PropertyClass> out_pc_it = rc.getOutgoingPropClasses();
            while (out_pc_it.hasNext()) {
                PropertyClass pc = out_pc_it.next();
                Iterator<ResourceClass> range_it = pc.getRange();
                while (range_it.hasNext()) {
                    ResourceClass range_rc = range_it.next();
                    if (range_rc.isDatatype()) {
                        String prop_name = this.addPrefix(pc.getNamespace())
                                + "_" + pc.getName();
                        String prop_value = range_rc.getName();
                        pg_schema.addNodeProperty(rc.hashCode(), prop_name, prop_value);
                    }

                }
            }
        }

        //process object properties
        Iterator<PropertyClass> properties = schema.getPropertyClasses();
        while (properties.hasNext()) {
            PropertyClass pc = properties.next();
            String prefix = this.addPrefix(pc.getNamespace());
            String prop_name = prefix + "_" + pc.getName();
            Iterator<ResourceClass> domain_it = pc.getDomain();
            while (domain_it.hasNext()) {
                ResourceClass rc1 = domain_it.next();
                Iterator<ResourceClass> range_it = pc.getRange();
                while (range_it.hasNext()) {
                    ResourceClass rc2 = range_it.next();
                    if (!rc2.isDatatype()) {
                        pg_schema.addEdge(prop_name, rc1.hashCode(), rc2.hashCode());
                    }
                }
            }

        }

        Integer nid = pg_schema.addNode("Namespace");
        pg_schema.addNodeProperty(nid, "iri", "String");
        pg_schema.addNodeProperty(nid, "prefix", "String");

        return pg_schema;
    }

    public PropertyGraph runInstanceMapping(String input_instance_filename) {
        try {
            InputStream in = FileManager.get().open(input_instance_filename);
            Reader3 reader3 = new Reader3(prefixes);
            RDFDataMgr.parse(reader3, in, Lang.TTL);
            pg_instance = reader3.getPG();
            if (in == null) {
                throw new IllegalArgumentException("File not found");
            }
        } catch (Exception ex) {
            System.out.println("Error SimpleMapping.run():" + ex.getMessage());
        }
        return pg_instance;
    }

    /*
    public PropertyGraph runInstanceMapping(String input_instance_filename) {
        int cnt = 0;

        System.out.println("Loading data to a Jena TDB disk-storage");
        Dataset dataset;
        DatasetGraphTDB dsg;
        try {
            InputStream in = FileManager.get().open(input_instance_filename);
            if (in == null) {
                throw new IllegalArgumentException("File: " + input_instance_filename + " not found");
            }
            FileUtils.deleteDirectory(new File("tdb"));
            dataset = TDBFactory.createDataset("tdb");
            dataset.begin(ReadWrite.WRITE);
            dsg = TDBInternal.getBaseDatasetGraphTDB(dataset.asDatasetGraph());
            RDFDataMgr.read(dsg, in, Lang.TURTLE);
            dataset.commit();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.out.println("The input file cannot be loaded to TDB.");
            return pg_instance;
        }
        System.out.println("OK");

        Resource subj;
        Resource pred;
        String subj_name;
        String pred_name;
        String obj_name;

        String queryString;
        Query query;
        QueryExecution qexec;
        ResultSet rs;

        System.out.println("Analysis of RDF triples");
        try {
            queryString = "select * where { ?s ?p ?o }";
            query = QueryFactory.create(queryString);
            qexec = QueryExecutionFactory.create(query, dsg);
            rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution qs = rs.nextSolution();
                RDFNode s = qs.get("s");
                RDFNode p = qs.get("p");
                RDFNode o = qs.get("o");
                cnt++;

                subj = s.asResource();
                PGNode snode = pg_instance.getNodeById(subj.hashCode());
                if (snode == null) {
                    snode = pg_instance.createNode();
                    snode.setId(subj.hashCode());
                    if (subj.isURIResource()) {
                        // the subject is a IRI
                        subj_name = this.getPrefix(subj.getNameSpace()) + "_" + subj.getLocalName();
                        pg_instance.createProperty(snode, "iri", subj.getURI());
                    } else {
                        // the subject is a blank node
                        pg_instance.createProperty(snode, "id", subj.getId().getLabelString());
                    }
                }

                pred = p.asResource();
                pred_name = this.getPrefix(pred.getNameSpace()) + "_" + pred.getLocalName();

                if (o.isResource()) {
                    //the object is a resource
                    Resource obj = o.asResource();
                    if (pred.getURI().compareTo(RDF.type.getURI()) == 0) {
                        // the predicate is rdf:type
                        String label_namespace = obj.getNameSpace();
                        String label_prefix = prefixes.get(label_namespace);
                        if (label_prefix == null) {
                            label_prefix = "ns" + free_prefix++;
                            prefixes.put(label_namespace, label_prefix);
                        }
                        String label_name = label_prefix + "_" + obj.getLocalName();
                        snode.addLabel(label_name);
                    } else {
                        // the predicate is different to rdf:type
                        PGNode tnode = pg_instance.getNodeById(obj.hashCode());
                        if (tnode == null) {
                            tnode = pg_instance.createNode();
                            tnode.setId(obj.hashCode());
                            if (obj.isURIResource()) {
                                // the object is a IRI
                                obj_name = this.getPrefix(obj.getNameSpace()) + "_" + obj.getLocalName();
                                pg_instance.createProperty(tnode, "iri", obj.getURI());
                            } else {
                                // the object is a blank node
                                pg_instance.createProperty(tnode, "id", obj.getId().getLabelString());
                            }
                        }

                        PGEdge edge = pg_instance.createEdge(snode, tnode);
                        edge.addLabel(pred_name);
                    }

                } else {
                    //the object is a literal
                    Literal obj = o.asLiteral();
                    pg_instance.createProperty(snode, pred_name, obj.getString());
                }

            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        //creates nodes for namespace
        for (HashMap.Entry<String, String> entry : prefixes.entrySet()) {
            PGNode node = pg_instance.createNode("Namespace");
            pg_instance.createProperty(node, "uri", entry.getKey());
            pg_instance.createProperty(node, "prefix", entry.getValue());
        }

        System.out.println("Number of triples: " + cnt);
        return pg_instance;
    }

    public PropertyGraph runInstanceMapping2(String input_instance_filename) {
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(input_instance_filename);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + input_instance_filename + " not found");
        }

        // read the model
        FileManager.get().readModel(model, input_instance_filename);

        Resource subj;
        Resource pred;
        String subj_name;
        String pred_name;
        String obj_name;

        StmtIterator sit = model.listStatements();
        while (sit.hasNext()) {
            Statement triple = sit.next();

            subj = triple.getSubject().asResource();
            PGNode snode = pg_instance.getNodeById(subj.hashCode());
            if (snode == null) {
                snode = pg_instance.createNode();
                snode.setId(subj.hashCode());
                if (subj.isURIResource()) {
                    // the subject is a IRI
                    subj_name = this.getPrefix(subj.getNameSpace()) + "_" + subj.getLocalName();
                    pg_instance.createProperty(snode, "iri", subj.getURI());
                } else {
                    // the subject is a blank node
                    pg_instance.createProperty(snode, "id", subj.getId().getLabelString());
                }
            }

            pred = triple.getPredicate().asResource();
            pred_name = this.getPrefix(pred.getNameSpace()) + "_" + pred.getLocalName();

            if (triple.getObject().isResource()) {
                //the object is a resource
                Resource obj = triple.getObject().asResource();
                if (pred.getURI().compareTo(RDF.type.getURI()) == 0) {
                    // the predicate is rdf:type
                    String label_namespace = obj.getNameSpace();
                    String label_prefix = prefixes.get(label_namespace);
                    if (label_prefix == null) {
                        label_prefix = "ns" + free_prefix++;
                        prefixes.put(label_namespace, label_prefix);
                    }
                    String label_name = label_prefix + "_" + obj.getLocalName();
                    snode.addLabel(label_name);
                } else {
                    // the predicate is different to rdf:type
                    PGNode tnode = pg_instance.getNodeById(obj.hashCode());
                    if (tnode == null) {
                        tnode = pg_instance.createNode();
                        tnode.setId(obj.hashCode());
                        if (obj.isURIResource()) {
                            // the object is a IRI
                            obj_name = this.getPrefix(obj.getNameSpace()) + "_" + obj.getLocalName();
                            pg_instance.createProperty(tnode, "iri", obj.getURI());
                        } else {
                            // the object is a blank node
                            pg_instance.createProperty(tnode, "id", obj.getId().getLabelString());
                        }
                    }

                    PGEdge edge = pg_instance.createEdge(snode, tnode);
                    edge.addLabel(pred_name);
                }

            } else {
                //the object is a literal
                Literal obj = triple.getObject().asLiteral();
                pg_instance.createProperty(snode, pred_name, obj.getString());
            }

        }

        //creates nodes for namespace
        for (HashMap.Entry<String, String> entry : prefixes.entrySet()) {
            PGNode node = pg_instance.createNode("Namespace");
            pg_instance.createProperty(node, "uri", entry.getKey());
            pg_instance.createProperty(node, "prefix", entry.getValue());
        }
        return pg_instance;
    }
     */
}
