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

public class PGProperty {
    
    private int id = 0;
    private String label = "";
    private String value = ""; 
    private String datatype = "string";
    private Object owner = null;
    
    public PGProperty(Object owner, String _label, String _value){
        this.owner = owner;
        this.label = _label;
        this.value = _value;
    }
    
    public void setId(int _id){
        this.id = _id;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setOwner(PGNode node){
        owner = node;
    }
    
    public void setOwner(PGEdge edge){
        owner = edge;
    }
    
    public Object getOwner(){
        return this.owner;
    }
    
    
    public void setLabel(String _label){
        this.label = _label;
    }
    
    public String getLabel(){
        return this.label;
    }
    
    public void setValue(String _value){
        this.value = _value;
    }
    public String getValue(){
        return this.value;
    }
    
    public void setDatatype(String _datatype){
        this.datatype = _datatype;
    }
    public String getDatatype(){
        return this.datatype;
    }
    
}
