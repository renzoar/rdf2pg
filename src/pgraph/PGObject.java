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
import java.util.Iterator;

public abstract class PGObject {
    
    protected int id = 0;
    protected ArrayList<String> labels = new ArrayList();
    protected ArrayList<PGProperty> properties = new ArrayList();

    public PGObject(){
    }
    
    
    public PGObject(int _id){
        id = _id;
    }
    
    public void setId(int _id){
        this.id = _id;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getLabel(){
        if(!labels.isEmpty()){
            return labels.get(0);
        }
        return null;
    }
    
    public void addLabel(String label){
        if(!labels.contains(label)){
            labels.add(label);
        }
    }
    
    public boolean emptyLabels(){
        return labels.isEmpty();
    }
    
    public int labelsCounter(){
        return labels.size();
    }
    
    public Iterator<String> getLabels(){
        return labels.iterator();
    }

    public void addProperty(PGProperty prop){
        if(!properties.contains(prop)){
            properties.add(prop);
        }
    }
    
    public void addProperty(String label, String value){
        PGProperty prop = new PGProperty(this,label,value);
        if(!properties.contains(prop)){
            properties.add(prop);
        }        
    }
    
    public int propertiesCounter(){
        return properties.size();
    }
    
    public boolean emptyProperties(){
        return properties.isEmpty();
    }
    
    public Iterator<PGProperty> getProperties(){
        return properties.iterator();
    }    
    
    public boolean hasProperty(String property_name){
        PGProperty prop;
        Iterator<PGProperty> it = this.properties.iterator();
        while(it.hasNext()){
            prop = it.next();
            if(prop.getLabel().compareTo(property_name)==0){
                return true;
            }
        }
        return false;
    }
    
}
