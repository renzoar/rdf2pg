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
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import pgraph.PGEdge;
import pgraph.PGNode;
import writers.PGWriter;

/**
 *
 * @author renzo
 */
public class Reader3b implements StreamRDF {
    int cnt = 0;
    private int pos = 1;
    public HashMap<Integer, Integer> pos_hash_map = new HashMap();
    public HashMap<Integer, PGNode> hash_node_map = new HashMap();
    PGWriter pgwriter;

    public HashMap<String, String> prefixes = new HashMap();
    public int free_prefix = 1;
    
    public Reader3b(PGWriter _pgwriter) {
        pgwriter = _pgwriter;
    }

    @Override
    public void start() {
    }

    @Override
    public void triple(Triple triple) {
        cnt++;
        Node s = triple.getSubject();
        int subj_pos = pos++;
        Node p = triple.getPredicate();
        int pred_pos = pos++;
        Node o = triple.getObject();
        int obj_pos = pos++;

        //the object is a resource
        if (o.isURI() || o.isBlank()) {
            if (p.getURI().compareTo(RDF.type.getURI()) != 0) {
                PGNode snode = hash_node_map.get(pos_hash_map.get(subj_pos));
                PGNode tnode = hash_node_map.get(pos_hash_map.get(obj_pos));
                String pred_name = this.addPrefix(p.getNameSpace()) + "_" + p.getLocalName();
                PGEdge pgedge = new PGEdge(pred_pos, snode.getId(), tnode.getId());
                pgedge.addLabel(pred_name);
                pgwriter.writeEdge(pgedge);
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
        System.out.println("Number of RDF triples processed: " + cnt);
    }

}
