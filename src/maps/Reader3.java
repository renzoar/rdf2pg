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

import java.util.HashMap;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import pgraph.PropertyGraph;

public class Reader3 implements StreamRDF {

    int cnt = 0;
    PropertyGraph pg_instance;
    private final HashMap<String, String> prefixes;
    private int free_prefix = 1;

    public Reader3(HashMap<String, String> schema_prefixes) {
        prefixes = (HashMap<String, String>) schema_prefixes.clone();
    }

    public PropertyGraph getPG() {
        return pg_instance;
    }

    @Override
    public void start() {
        pg_instance = new PropertyGraph();
    }

    private String addPrefix(String namespace) {
        if (prefixes.containsKey(namespace)) {
            return prefixes.get(namespace);
        }
        String new_prefix = "nsi" + free_prefix++;
        prefixes.put(namespace, new_prefix);
        return new_prefix;
    }

    @Override
    public void triple(Triple triple) {
        Node s = triple.getSubject();
        Node p = triple.getPredicate();
        Node o = triple.getObject();

        if (!pg_instance.hasNode(s.hashCode())) {
            pg_instance.addNode(s.hashCode());
            if (s.isURI()) {
                pg_instance.addNodeProperty(s.hashCode(), "iri", s.getURI());
            } else if (s.isBlank()) {
                String id = "_:b" + s.hashCode();
                pg_instance.addNodeProperty(s.hashCode(), "id", id);

            } else {
                System.out.println("Error (Reader3): Invalid RDF triple");
                System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
            }
        }

        String pred_name = this.addPrefix(p.getNameSpace()) + "_" + p.getLocalName();

        if (p.getURI().compareTo(RDF.type.getURI()) == 0) {
            // the predicate is rdf:type
            if (o.isURI()) {
                String label_prefix = this.addPrefix(o.getNameSpace());
                String label_name = label_prefix + "_" + o.getLocalName();
                pg_instance.addNodeLabel(s.hashCode(), label_name);
            } else {
                System.out.println("Error (Reader3): Invalid RDF triple");
                System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
            }
        } else {
            // the predicate is different to rdf:type
            if (o.isURI()) {
                if (!pg_instance.hasNode(o.hashCode())) {
                    pg_instance.addNode(o.hashCode());
                    pg_instance.addNodeProperty(o.hashCode(), "iri", o.getURI());
                }
                pg_instance.addEdge(triple.hashCode(), pred_name, s.hashCode(), o.hashCode());
            } else if (o.isBlank()) {
                if (!pg_instance.hasNode(o.hashCode())) {
                    pg_instance.addNode(o.hashCode());
                    String id = "_:b" + s.hashCode();
                    pg_instance.addNodeProperty(o.hashCode(), "id", id);
                }
                pg_instance.addEdge(triple.hashCode(), pred_name, s.hashCode(), o.hashCode());
            } else if (o.isLiteral()) {
                pg_instance.addNodeProperty(s.hashCode(), pred_name, o.getLiteralValue().toString());
            } else {
                System.out.println("Error (Reader3): Invalid RDF triple");
                System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
            }
        }

    }

    private String getNodeString(Node node) {
        if (node.isURI()) {
            return node.getURI();
        }
        if (node.isBlank()) {
            return node.getBlankNodeLabel();
        }
        if (node.isLiteral()) {
            return node.getLiteral().toString();
        }
        return "";
    }

    @Override
    public void quad(Quad quad) {
        //System.out.println("quad");
    }

    @Override
    public void base(String string) {
        //System.out.println("base");
    }

    @Override
    public void prefix(String string, String string1) {
        //System.out.println("prefix");
    }

    @Override
    public void finish() {
        //creates nodes for namespace
        for (HashMap.Entry<String, String> entry : prefixes.entrySet()) {
            Integer node_id = pg_instance.addNode("Namespace");
            pg_instance.addNodeProperty(node_id, "iri", entry.getKey());
            pg_instance.addNodeProperty(node_id, "prefix", entry.getValue());
        }
        
    }

}
