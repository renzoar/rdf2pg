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
package pgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author renzo
 */
public class PGSchema {
    private int oid = 1;
    private ArrayList<PGNode> nodes = new ArrayList();
    private ArrayList<PGEdge> edges = new ArrayList();
    
    public void addPGNode(PGNode node){
        nodes.add(node);
    }
    
    public Iterator<PGNode> getNodes(){
        return nodes.iterator();
    }
    
    public void addPGEdge(PGEdge edge){
        edges.add(edge);
    }
    
    public Iterator<PGEdge> getEdges(){
        return edges.iterator();
    }
    
}
