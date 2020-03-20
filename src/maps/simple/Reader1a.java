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
package maps.simple;

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
public class Reader1a implements StreamRDF {
    private int pos = 1;
    public HashMap<Integer, Integer> pos_hash_map;
    public HashMap<Integer, PGNode> hash_node_map;
    private PGWriter pgwriter;

    public Reader1a(PGWriter _pgwriter) {
        pos_hash_map = new HashMap();
        hash_node_map = new HashMap();
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
        }else{
            pos_hash_map.put(subj_pos, s.hashCode());
        }

        //the object is a resource
        if (o.isURI() || o.isBlank()) {
            if (p.getURI().compareTo(RDF.type.getURI()) == 0) {
                String label = o.getLocalName();
                snode.addLabel(label);
            } else {
                PGNode tnode = hash_node_map.get(o.hashCode());
                if (tnode == null) {
                    tnode = new PGNode(obj_pos);
                    pos_hash_map.put(obj_pos, o.hashCode());
                    hash_node_map.put(o.hashCode(), tnode);
                }else{
                    pos_hash_map.put(obj_pos, o.hashCode());
                }
            }
        } else {
            snode.addProperty(p.getLocalName(), o.getLiteral().getValue().toString());
        }
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
