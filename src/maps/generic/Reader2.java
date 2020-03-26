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
package maps.generic;

import java.util.HashMap;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import pgraph.PGNode;
import pgraph.PGEdge;
import writers.PGWriter;

public class Reader2 implements StreamRDF {
    int oid = 1;
    int cnt = 0;
    PGWriter pgwriter;
    //HashSet<Integer> nodeset = new HashSet();
    HashMap<Integer,PGNode> hash_node_map = new HashMap();
    
    
    public Reader2(PGWriter _pgwriter) {
        this.pgwriter = _pgwriter;
    }

    @Override
    public void start() {
    }

    @Override
    public void triple(Triple triple) {
        cnt++;
        Node s = triple.getSubject();
        Node p = triple.getPredicate();
        Node o = triple.getObject();
        //System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));
        
        PGNode snode = hash_node_map.get(s.hashCode());
        if (snode == null) {
            if (s.isURI()) {
                snode = new PGNode(oid++);
                snode.addLabel("Resource");
                snode.addProperty("iri", s.getURI());
            } else if(s.isBlank()) {
                snode = new PGNode(oid++);
                snode.addLabel("BlankNode");
                String id = "_:b" + s.hashCode();
                snode.addProperty("id", id);
            } else{
                System.out.println("Error in Reader2.java");
                System.out.println("Invalid triple");
            }
            hash_node_map.put(s.hashCode(), snode);
            pgwriter.writeNode(snode);
        }
        
        if (o.isURI() || o.isBlank()) {
            PGNode tnode = hash_node_map.get(o.hashCode());
            if (tnode ==  null) {
                if (o.isURI()) {
                    tnode = new PGNode(oid++);
                    tnode.addLabel("Resource");
                    tnode.addProperty("iri", o.getURI());
                } else {
                    tnode = new PGNode(oid++);
                    tnode.addLabel("BlankNode");
                    String id = "_:b" + o.hashCode();
                    tnode.addProperty("id", id);
                }
                hash_node_map.put(o.hashCode(), tnode);
                pgwriter.writeNode(tnode);
            }
            PGEdge edge = new PGEdge(oid++,snode.getId(),tnode.getId());
            edge.addLabel("ObjectProperty");
            edge.addProperty("type", p.getURI());
            pgwriter.writeEdge(edge);
        } else {
            //the object is a literal 
            PGNode tnode = new PGNode(oid++);
            tnode.addLabel("Literal");
            tnode.addProperty("value", o.getLiteral().getValue().toString());
            tnode.addProperty("type", o.getLiteral().getDatatypeURI());
            pgwriter.writeNode(tnode);
            
            PGEdge edge = new PGEdge(oid++,snode.getId(),tnode.getId());
            edge.addLabel("DatatypeProperty");
            edge.addProperty("type", p.getURI());
            pgwriter.writeEdge(edge);
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
        System.out.println("Number of RDF triples processed: " + cnt);
    }

}
