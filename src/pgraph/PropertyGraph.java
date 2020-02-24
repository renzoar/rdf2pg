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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class PropertyGraph {

    HashMap<Integer, String> nodes = new HashMap();
    HashMap<Integer, String> edges = new HashMap();
    HashMap<Integer, String> nodeprops = new HashMap();
    HashMap<Integer, String> edgeprops = new HashMap();
    HashMap<Integer, Integer> sourcenodes = new HashMap();
    HashMap<Integer, Integer> targetnodes = new HashMap();
    Integer free_id = 1;

    public PropertyGraph() {
    }

    public boolean hasNode(int id) {
        return nodes.containsKey(id);
    }
    
    public Integer addNode() {
        Integer id = free_id;
        free_id++;
        nodes.putIfAbsent(id, "null");
        return id;
    }

    public void addNode(int id) {
        nodes.putIfAbsent(id, "null");
    }
    
    public Integer addNode(String label){
        Integer id = free_id;
        free_id++;
        nodes.putIfAbsent(id, label);
        return id;
    }
    
    public Integer addNode(int id, String label){
        nodes.putIfAbsent(id, label);
        return id;
    }    

    public void addNodeLabel(int node_id, String label) {
        String labels = nodes.get(node_id);
        if (labels.compareTo("null") == 0) {
            nodes.put(node_id, label);
        } else {
            String new_labels = labels + "," + label;
            nodes.replace(node_id, new_labels);
        }
    }

    public void addNodeProperty(int node_id, String prop_name, String prop_value) {
        String new_props;
        String old_props = nodeprops.get(node_id);
        if (old_props == null) {
            new_props = prop_name + ":\"" + prop_value + "\"";
            nodeprops.put(node_id, new_props);
        } else {
            new_props = old_props + "," + prop_name + ":\"" + prop_value + "\"";
            nodeprops.replace(node_id, new_props);
        }
    }

    public Integer addEdge(int source, int target) {
        Integer id = free_id;
        free_id++;
        edges.put(id, "null");
        sourcenodes.put(id, source);
        targetnodes.put(id, target);
        return id;
    }
    
    public Integer addEdge(String label, int source, int target) {
        Integer id = free_id;
        free_id++;
        edges.put(id, label);
        sourcenodes.put(id, source);
        targetnodes.put(id, target);
        return id;
    }

    public Integer addEdge(Integer id, String label, int source, int target) {
        edges.put(id, label);
        sourcenodes.put(id, source);
        targetnodes.put(id, target);
        return id;
    }
    
    
    public void addEdgeLabel(int edge_id, String label) {
        String labels = edges.get(edge_id);
        if (labels.compareTo("null") == 0) {
            edges.put(edge_id, label);
        } else {
            String new_labels = labels + "," + label;
            edges.replace(edge_id, new_labels);
        }

    }
    
    public void addEdgeProperty(int edge_id, String prop_name, String prop_value) {
        String new_props;
        String old_props = edgeprops.get(edge_id);
        if (old_props == null) {
            new_props = prop_name + ":\"" + prop_value + "\"";
            edgeprops.put(edge_id, new_props);
        } else {
            new_props = old_props + "," + prop_name + ":\"" + prop_value + "\"";
            edgeprops.replace(edge_id, new_props);
        }
    }    

    public void exportAsYPG(String outputFileName) {
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8"));

            //export nodes
            for (Map.Entry<Integer, String> entry : nodes.entrySet()) {
                Integer nid = entry.getKey();
                String labels = entry.getValue();
                String props = nodeprops.get(nid);
                if (labels.compareTo("null") == 0) {
                    labels = "[]";
                } else {
                    labels = "[" + labels + "]";
                }
                if (null == props) {
                    props = "{}";
                } else {
                    props = "{" + props + "}";
                }
                writer.write("N(" + nid + ")" + labels + props + "\n");
            }

            //export edges
            for (Map.Entry<Integer, String> entry : edges.entrySet()) {
                Integer eid = entry.getKey();
                String labels = entry.getValue();
                String props = edgeprops.get(eid);
                Integer source = sourcenodes.get(eid);
                Integer target = targetnodes.get(eid);
                if (labels.compareTo("null") == 0) {
                    labels = "[]";
                } else {
                    labels = "[" + labels + "]";
                }
                if (null == props) {
                    props = "{}";
                } else {
                    props = "{" + props + "}";
                }
                writer.write("E(" + source + ")(" + target + ")" + labels + props + "\n");
            }

            writer.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

    /*
    public Iterator<PGNode> getNodes() {
        return nodes.iterator();
    }

    public Iterator<PGEdge> getEdges() {
        return edges.iterator();
    }

    public Iterator<PGProperty> getProperties() {
        return properties.iterator();
    }

    public PGNode createNode() {
        PGNode node = new PGNode();
        node.setId(free_id++);
        nodes.add(node);
        return node;
    }

    public PGNode createNode(String label) {
        PGNode node = new PGNode();
        node.setId(free_id++);
        node.addLabel(label);
        nodes.add(node);
        return node;
    }

    public PGEdge createEdge(PGNode source, PGNode target) {
        PGEdge edge = new PGEdge(source, target);
        edge.setId(free_id++);
        edges.add(edge);
        return edge;
    }

    public PGEdge createEdge(String label, PGNode source, PGNode target) {
        PGEdge edge = new PGEdge(label, source, target);
        edge.setId(free_id++);
        this.edges.add(edge);
        return edge;
    }

    public PGProperty createProperty(PGNode owner, String name, String value) {
        PGProperty prop = new PGProperty(owner, name, value);
        prop.setId(free_id++);
        this.properties.add(prop);
        if (!nodes.contains(owner)) {
            nodes.add(owner);
        }
        prop.setOwner(owner);
        owner.addProperty(prop);
        return prop;
    }

    public PGProperty createProperty(PGEdge owner, String name, String value) {
        PGProperty prop = new PGProperty(owner, name, value);
        prop.setId(free_id++);
        this.properties.add(prop);
        if (!edges.contains(owner)) {
            edges.add(owner);
        }
        prop.setOwner(owner);
        owner.addProperty(prop);
        return prop;
    }

    public PGNode getNodeById(int id) {
        Iterator<PGNode> it = this.nodes.iterator();
        while (it.hasNext()) {
            PGNode node = it.next();
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

    public PGEdge getEdgeById(int id) {
        Iterator<PGEdge> it = this.edges.iterator();
        while (it.hasNext()) {
            PGEdge edge = it.next();
            if (edge.id == id) {
                return edge;
            }
        }
        return null;
    }

    public PGProperty getPropertyById(int id) {
        Iterator<PGProperty> it = this.properties.iterator();
        while (it.hasNext()) {
            PGProperty prop = it.next();
            if (prop.getId() == id) {
                return prop;
            }
        }
        return null;
    }
     */
 /*
    public void addNode(Node node){
        if(!nodes.contains(node)){
            nodes.add(node);
            node.setId(id++);
            Iterator<Property> it = node.getProperties();
            while(it.hasNext()){
                Property pt = it.next();
                this.addProperty(pt);
            }
        }
    }*/
 /*
    public void AddEdge(Edge edge){
        if(!edges.contains(edge)){
            edges.add(edge);
            this.addNode(edge.getSourceNode());
            this.addNode(edge.getTargetNode());            
            Iterator<Property> it = edge.getProperties();
            while(it.hasNext()){
                Property pt = it.next();
                this.addProperty(pt);
            }
        }
    }*/
 /*
    private void addProperty(Property property){
        if(!propertys.contains(property)){
            propertys.add(property);
            property.setId(id++);
        }
    }*/
}
