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

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import pgraph.PGNode;
import writers.PGWriter;

/**
 *
 * @author renzo
 */
public class Reader3a implements StreamRDF {

    private int pos = 1;
    public HashMap<Integer, Integer> pos_hash_map = new HashMap();
    public HashMap<Integer, PGNode> hash_node_map = new HashMap();
    private PGWriter pgwriter;

    public HashMap<String, String> prefixes = new HashMap();
    public int free_prefix = 1;

    public Reader3a(PGWriter _pgwriter) {
        pgwriter = _pgwriter;
    }

    @Override
    public void start() {
    }

    @Override
    public void triple(Triple triple) {
        Node s = triple.getSubject();
        int subj_pos = pos++;
        Node p = triple.getPredicate();
        int pred_pos = pos++;
        Node o = triple.getObject();
        int obj_pos = pos++;

        PGNode snode = hash_node_map.get(s.hashCode());
        if (snode == null) {
            snode = new PGNode(subj_pos);
            pos_hash_map.put(subj_pos, s.hashCode());
            hash_node_map.put(s.hashCode(), snode);
            if (s.isURI()) {
                snode.addProperty("iri", s.getURI());
            } else if (s.isBlank()) {
                String id = "_:b" + s.hashCode();
                snode.addProperty("id", id);
            } else {
                System.out.println("Error: Invalid RDF triple");
                System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
            }
        } else {
            pos_hash_map.put(subj_pos, s.hashCode());
        }

        if (p.getURI().compareTo(RDF.type.getURI()) == 0) {
            // the predicate is rdf:type
            if (o.isURI()) {
                String label_prefix = this.addPrefix(o.getNameSpace());
                String label_name = label_prefix + "_" + o.getLocalName();
                snode.addLabel(label_name);
            } else {
                System.out.println("Error: Invalid RDF triple");
                System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
            }
        } else {
            // the predicate is different to rdf:type
            PGNode tnode = hash_node_map.get(o.hashCode());
            if (tnode == null) {
                if (o.isURI()) {
                    tnode = new PGNode(obj_pos);
                    pos_hash_map.put(obj_pos, o.hashCode());
                    hash_node_map.put(o.hashCode(), tnode);
                    tnode.addProperty("iri", o.getURI());
                } else if (o.isBlank()) {
                    tnode = new PGNode(obj_pos);
                    pos_hash_map.put(obj_pos, o.hashCode());
                    hash_node_map.put(o.hashCode(), tnode);
                    String id = "_:b" + o.hashCode();
                    tnode.addProperty("id", id);
                } else if (o.isLiteral()) {
                    String pred_name = this.addPrefix(p.getNameSpace()) + "_" + p.getLocalName();
                    snode.addProperty(pred_name, o.getLiteralValue().toString());
                } else {
                    System.out.println("Error: Invalid RDF triple");
                    System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
                }
            } else {
                pos_hash_map.put(obj_pos, o.hashCode());
            }
        }
    }

    private String addPrefix(String namespace) {
        if (prefixes.containsKey(namespace)) {
            return prefixes.get(namespace);
        }
        String new_prefix = "nsi" + free_prefix++;
        prefixes.put(namespace, new_prefix);
        return new_prefix;
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
        for (Map.Entry<Integer, PGNode> entry : hash_node_map.entrySet()) {
            PGNode pgnode = entry.getValue();
            pgwriter.writeNode(pgnode);
        }

    }

}
