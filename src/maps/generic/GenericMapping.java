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

import java.io.InputStream;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import pgraph.PGEdge;
import pgraph.PGNode;
import pgraph.PropertyGraph;
import writers.PGWriter;
import writers.YPGWriter;

public class GenericMapping {

    PropertyGraph pg_instance;
    PropertyGraph pg_schema;

    public GenericMapping() {
        pg_instance = new PropertyGraph();
        pg_schema = new PropertyGraph();
    }

    public PropertyGraph getPGInstance() {
        return pg_instance;
    }

    public PropertyGraph getPGSchema() {
        return pg_schema;
    }

    public void run(String inputFileName) {
        PGWriter instance_pgwriter = new YPGWriter("instance.ypg");
        PGWriter schema_pgwriter = new YPGWriter("schema.ypg");
        this.run(inputFileName, instance_pgwriter, schema_pgwriter);
    }

    public void run(String inputFileName, PGWriter instance_pgwriter, PGWriter schema_pgwriter) {
        this.runInstanceMapping(inputFileName, instance_pgwriter);
        this.runSchemaMapping(schema_pgwriter);
    }

    public void runInstanceMapping(String inputFileName, PGWriter pgwriter) {
        try {
            InputStream in = FileManager.get().open(inputFileName);
            pgwriter.begin();
            Reader2 reader = new Reader2(pgwriter);
            RDFDataMgr.parse(reader, in, Lang.TTL);
            if (in == null) {
                throw new IllegalArgumentException("File not found");
            }
            pgwriter.end();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void runSchemaMapping(PGWriter pgwriter) {
        try {
            pgwriter.begin();

            PGNode rnode = new PGNode(1);
            rnode.addLabel("Resource");
            rnode.addProperty("iri", "String");
            pgwriter.writeNode(rnode);
            
            PGNode bnode = new PGNode(2);
            bnode.addLabel("BlankNode");
            bnode.addProperty("id", "String");
            pgwriter.writeNode(bnode);

            PGNode lnode = new PGNode(3);
            lnode.addLabel("Literal");
            lnode.addProperty("value", "String");
            lnode.addProperty("type", "String");
            pgwriter.writeNode(lnode);
            
            PGEdge edge4 = new PGEdge(4,rnode.getId(),rnode.getId());
            edge4.addLabel("ObjectProperty");
            edge4.addProperty("type","String");
            pgwriter.writeEdge(edge4);

            PGEdge edge5 = new PGEdge(5,rnode.getId(),bnode.getId());
            edge5.addLabel("ObjectProperty");
            edge5.addProperty("type","String");
            pgwriter.writeEdge(edge5);
            
            PGEdge edge6 = new PGEdge(6,bnode.getId(),rnode.getId());
            edge6.addLabel("ObjectProperty");
            edge6.addProperty("type","String");
            pgwriter.writeEdge(edge6);

            PGEdge edge7 = new PGEdge(7,bnode.getId(),bnode.getId());
            edge7.addLabel("ObjectProperty");
            edge7.addProperty("type","String");
            pgwriter.writeEdge(edge7);
            
            PGEdge edge8 = new PGEdge(8,rnode.getId(),lnode.getId());
            edge8.addLabel("DatatypeProperty");
            edge8.addProperty("type","String");
            pgwriter.writeEdge(edge8);

            PGEdge edge9 = new PGEdge(9,bnode.getId(),lnode.getId());
            edge9.addLabel("DatatypeProperty");
            edge9.addProperty("type","String");
            pgwriter.writeEdge(edge9);
            
            pgwriter.end();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

    }

}
