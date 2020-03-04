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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import pgraph.PropertyGraph;

public class Reader2 implements StreamRDF {
    int cnt = 0;
    PropertyGraph pg;

    public Reader2() {

    }

    public PropertyGraph getPG() {
        return pg;
    }

    @Override
    public void start() {
        pg = new PropertyGraph();
    }

    @Override
    public void triple(Triple triple) {
        cnt++;
        Node s = triple.getSubject();
        Node p = triple.getPredicate();
        Node o = triple.getObject();
        //System.out.println(this.getNodeString(s) + " - " + this.getNodeString(p) + " - " + this.getNodeString(o));

        if (!pg.hasNode(s.hashCode())) {
            if (s.isURI()) {
                pg.addNode(s.hashCode(), "Resource");
                pg.addNodeProperty(s.hashCode(), "iri", s.getURI());
            } else {
                pg.addNode(s.hashCode(), "BlankNode");
                String id = "_:b" + s.hashCode();
                pg.addNodeProperty(s.hashCode(), "id", id);
            }
        }

        if (o.isURI() || o.isBlank()) {
            //the object is a resource (URI or BNode)
            if (!pg.hasNode(o.hashCode())) {
                if (o.isURI()) {
                    pg.addNode(o.hashCode(), "Resource");
                    pg.addNodeProperty(o.hashCode(), "iri", o.getURI());
                } else {
                    pg.addNode(o.hashCode(), "BlankNode");
                    String id = "_:b" + o.hashCode();
                    pg.addNodeProperty(o.hashCode(), "id", id);
                }
            }
            pg.addEdge(triple.hashCode(),"ObjectProperty", s.hashCode(), o.hashCode());
            pg.addEdgeProperty(triple.hashCode(), "type", p.getURI());

        } else {
            //the object is a literal 
            Integer nid = pg.addNode("Literal");
            pg.addNodeProperty(nid, "value", o.getLiteral().getValue().toString());
            pg.addNodeProperty(nid, "type", o.getLiteral().getDatatypeURI());
            Integer eid = pg.addEdge("DatatypeProperty", s.hashCode(), nid);
            pg.addEdgeProperty(eid, "type", p.getURI());
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
        //System.out.println("finish");
        System.out.println("Number of RDF triples processed: " + cnt);
    }

}
