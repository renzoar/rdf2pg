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

import java.io.InputStream;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import pgraph.PropertyGraph;

public class GeneralDatabaseMapping {

    PropertyGraph pg_instance;
    PropertyGraph pg_schema;

    public GeneralDatabaseMapping() {
        pg_instance = new PropertyGraph();
        pg_schema = new PropertyGraph();
    }

    public void run(String RDF_filename) {
        this.runInstanceMapping(RDF_filename);
        this.runSchemaMapping();
    }

    public PropertyGraph getPGInstance() {
        return pg_instance;
    }

    public PropertyGraph getPGSchema() {
        return pg_schema;
    }
    
    public void runSchemaMapping() {
        Integer resource_id = pg_schema.addNode("Resource");
        pg_schema.addNodeProperty(resource_id,"iri", "String");
        Integer bnode_id = pg_schema.addNode("BlankNode");
        pg_schema.addNodeProperty(bnode_id, "id", "String");
        Integer literal_id = pg_schema.addNode("Literal");
        pg_schema.addNodeProperty(literal_id, "value", "String");
        pg_schema.addNodeProperty(literal_id, "type", "String");
        Integer e1 = pg_schema.addEdge("ObjectProperty", resource_id, resource_id);
        pg_schema.addEdgeProperty(e1, "type", "String");
        Integer e2 = pg_schema.addEdge("ObjectProperty", resource_id, bnode_id);
        pg_schema.addEdgeProperty(e2, "type", "String");
        Integer e3 = pg_schema.addEdge("ObjectProperty", bnode_id, resource_id);
        pg_schema.addEdgeProperty(e3, "type", "String");
        Integer e4 = pg_schema.addEdge("ObjectProperty", bnode_id, bnode_id);
        pg_schema.addEdgeProperty(e4, "type", "String");
        Integer e5 = pg_schema.addEdge("DatatypeProperty", resource_id, literal_id);
        pg_schema.addEdgeProperty(e5, "type", "String");
        Integer e6 = pg_schema.addEdge("DatatypeProperty", bnode_id, literal_id);
        pg_schema.addEdgeProperty(e6, "type", "String");
    }
    
    

    public void runInstanceMapping(String inputFileName) {
        try {
            InputStream in = FileManager.get().open(inputFileName);
            Reader2 reader = new Reader2();
            RDFDataMgr.parse(reader, in, Lang.TTL);
            pg_instance = reader.getPG();
            if (in == null) {
                throw new IllegalArgumentException("File not found");
            }
        } catch (Exception ex) {
            System.out.println("Error runInstanceMapping():" + ex.getMessage());
        }            
    }

 

}
