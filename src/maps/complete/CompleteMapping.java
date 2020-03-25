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
package maps.complete;

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
import pgraph.PGEdge;
import pgraph.PGNode;
import pgraph.PGSchema;
import pgraph.PropertyGraph;
import writers.PGWriter;
import writers.YPGWriter;

/**
 *
 * @author renzo
 */
public class CompleteMapping {

    private PGSchema pg_schema;
    private HashMap<String, String> prefixes;
    private int free_prefix = 1;

    public void run(String input_instance_filename, String input_schema_filename) {
        PGWriter schema_pgwriter = new YPGWriter("schema.ypg");
        PGWriter instance_pgwriter = new YPGWriter("instance.ypg");
        this.run(input_instance_filename, input_schema_filename, instance_pgwriter, schema_pgwriter);
    }
    
    public void run(String input_instance_filename, String input_schema_filename, PGWriter instance_pgwriter, PGWriter schema_pgwriter) {
        this.runSchemaMapping(input_schema_filename, schema_pgwriter);
        this.runInstanceMapping(input_instance_filename, instance_pgwriter);
    }
    
    public void runSchemaMapping(String input_schema_filename, PGWriter pgwriter) {
        HashMap<Integer,Integer> hash_id_map = new HashMap();
        int oid = 1;

        SchemaReader schema_reader = new SchemaReader();
        Schema schema = schema_reader.run(input_schema_filename);
        
        PGNode pgnode_ns = new PGNode(oid++);
        pgnode_ns.addLabel("Namespace");
        pgnode_ns.addProperty("iri","String");
        pgnode_ns.addProperty("prefix","String");
        pg_schema.addPGNode(pgnode_ns);        

        //Process value properties
        Iterator<ResourceClass> classes = schema.getResourceClasses();
        while (classes.hasNext()) {
            ResourceClass rc = classes.next();
            if (rc.isDatatype()) {
                continue;
            }
            String class_name = this.addPrefix(rc.getNamespace()) + "_" + rc.getName();
            
            int node_id = oid++;
            hash_id_map.put(rc.hashCode(), node_id);
            PGNode pgnode = new PGNode(node_id);
            pgnode.addLabel(class_name);
            pg_schema.addPGNode(pgnode);
            
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
                        pgnode.addProperty(prop_name, prop_value);
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
                        int edge_id = oid++;
                        int snode_id = hash_id_map.get(rc1.hashCode());
                        int tnode_id = hash_id_map.get(rc2.hashCode());
                        PGEdge pgedge = new PGEdge(edge_id, snode_id, tnode_id);
                        pgedge.addLabel(prop_name);
                        pg_schema.addPGEdge(pgedge);
                    }
                }
            }

        }
                
        pgwriter.begin();
        Iterator<PGNode> it1 = pg_schema.getNodes();
        while(it1.hasNext()){
            PGNode pgnode = it1.next();
            pgwriter.writeNode(pgnode);
        }
        Iterator<PGEdge> it2 = pg_schema.getEdges();
        while(it2.hasNext()){
            PGEdge pgedge = it2.next();
            pgwriter.writeEdge(pgedge);
        }
        pgwriter.end();
    }

    public void runInstanceMapping(String input_instance_filename, PGWriter pgwriter) {
        HashMap<Integer, Integer> pos_hash_map = new HashMap();
        HashMap<Integer, PGNode> hash_node_map = new HashMap();

        try {
            pgwriter.begin();

            Reader3a reader3a = new Reader3a(pgwriter);
            reader3a.pos_hash_map = pos_hash_map;
            reader3a.hash_node_map = hash_node_map;
            reader3a.prefixes = this.prefixes;
            reader3a.free_prefix = this.free_prefix;
            InputStream in1 = FileManager.get().open(input_instance_filename);
            RDFDataMgr.parse(reader3a, in1, Lang.TTL);
            if (in1 == null) {
                throw new IllegalArgumentException("File not found");
            }
            
            Reader3b reader3b = new Reader3b(pgwriter);
            reader3b.pos_hash_map = pos_hash_map;
            reader3b.hash_node_map = hash_node_map;
            reader3b.prefixes = this.prefixes;
            reader3b.free_prefix = this.free_prefix;
            InputStream in2 = FileManager.get().open(input_instance_filename);
            RDFDataMgr.parse(reader3b, in2, Lang.TTL);
            if (in2 == null) {
                throw new IllegalArgumentException("File not found");
            }

            pgwriter.end();
        } catch (Exception ex) {
            System.out.println("Error in runInstanceMapping()");
            System.out.println(ex.getMessage());
        }
    }

    public CompleteMapping() {
        pg_schema = new PGSchema();
        prefixes = new HashMap();
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://www.w3.org/2002/07/owl#", "owl");
        prefixes.put("http://www.w3.org/2001/XMLSchema#", "xsd");
    }

    private String addPrefix(String namespace) {
        if (prefixes.containsKey(namespace)) {
            return prefixes.get(namespace);
        }
        String new_prefix = "nss" + free_prefix++;
        prefixes.put(namespace, new_prefix);
        return new_prefix;
    }
}
