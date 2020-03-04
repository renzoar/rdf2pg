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
import org.apache.jena.vocabulary.RDF;
import pgraph.PropertyGraph;

public class Reader1 implements StreamRDF {
    int cnt = 0;
    PropertyGraph pg;

    public Reader1() {

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
        
        if(!pg.hasNode(s.hashCode())){
            pg.addNode(s.hashCode());
        }

        //the object is a resource
        if (o.isURI() || o.isBlank()) {
            if (p.getURI().compareTo(RDF.type.getURI()) == 0) {
                String label = o.getLocalName();
                pg.addNodeLabel(s.hashCode(), label);
            } else {
                if(!pg.hasNode(o.hashCode())){
                    pg.addNode(o.hashCode());
                }
                
                int edge_id = pg.addEdge(s.hashCode(), o.hashCode());
                pg.addEdgeLabel(edge_id, p.getLocalName());
            }
        } else {
            pg.addNodeProperty(s.hashCode(), p.getLocalName(), o.getLiteral().getValue().toString());
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

