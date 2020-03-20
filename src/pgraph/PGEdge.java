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

public class PGEdge extends PGObject{
    
    private final int source_node;
    private final int target_node;
    
    public PGEdge(int _id, int source_node_id, int target_node_id){
        this.id = _id;
        this.source_node = source_node_id;
        this.target_node = target_node_id;
    }
    
    public int getSourceNode(){
        return this.source_node;
    }
    
    public int getTargetNode(){
        return this.target_node;
    }
    
}