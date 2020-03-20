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
    HashMap<Integer,Integer> map = new HashMap();

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
        Integer id = 1;
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8"));

            //export nodes
            for (Map.Entry<Integer, String> entry : nodes.entrySet()) {
                Integer key = entry.getKey();
                map.put(key, id);
                Integer nid = id;
                id++;
                String labels = entry.getValue();
                String props = nodeprops.get(key);
                if (labels.compareTo("null") == 0) {
                    labels = "";
                } else {
                    labels = "[" + labels.replace(",",":") + "]";
                }
                if (null == props) {
                    props = "{}";
                } else {
                    props = "{" + props + "}";
                }
                writer.write(nid + labels + ":" + props + "\n");
            }

            //export edges
            for (Map.Entry<Integer, String> entry : edges.entrySet()) {
                Integer key = entry.getKey();
                Integer eid = id;
                id++;
                String labels = entry.getValue();
                String props = edgeprops.get(key);
                Integer source = map.get(sourcenodes.get(key));
                Integer target = map.get(targetnodes.get(key));
                if (labels.compareTo("null") == 0) {
                    labels = "";
                }else{
                    labels = labels.replace(",",":");
                }
                if (null == props) {
                    writer.write("(" + source + ")-[" + labels + "]->(" + target + ")" + "\n");
                } else {
                    writer.write("(" + source + ")-[" + labels + " {" + props + "}]->(" + target + ")" + "\n");
                }
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public void exportAsPGF(String outputFileName) {
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
    
    public void exportAsCypher(String outputFileName){
        Integer nextid = 1;
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8"));

            //export nodes
            for (Map.Entry<Integer, String> entry : nodes.entrySet()) {
                Integer nid = nextid++;
                map.put(entry.getKey(),nid);
                String labels = entry.getValue();
                String props = nodeprops.get(entry.getKey());
                if (labels.compareTo("null") == 0) {
                    labels = "";
                } else {
                    labels = ":" + labels.replace(",",":");
                }
                if (null == props) {
                    props = "";
                } else {
                    props = "{" + props + "}";
                }
                writer.write("CREATE (n" + nid + labels + props + ")\n");
            }

            //export edges
            for (Map.Entry<Integer, String> entry : edges.entrySet()) {
                Integer eid = nextid++;
                Integer source = map.get(sourcenodes.get(entry.getKey()));
                Integer target = map.get(targetnodes.get(entry.getKey()));
                String labels = entry.getValue();
                String props = edgeprops.get(entry.getKey());
                if (labels.compareTo("null") == 0) {
                    labels = "";
                } else {
                    labels = ":" + labels.replace(",",":");
                }
                if (null == props) {
                    props = "";
                } else {
                    props = "{" + props + "}";
                }
                writer.write("CREATE (n" + source + ")-[e" + eid + labels + props + "]->(n" + target + ")\n");
            }
            writer.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    

}
