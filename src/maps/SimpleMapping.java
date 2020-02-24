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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import pgraph.PropertyGraph;

public class SimpleMapping {

    private String getString(RDFNode node) {
        if (node.isURIResource()) {
            return node.asResource().getURI();
        } else if (node.isAnon()) {
            return node.asResource().getId().getLabelString();
        } else {
            return node.asLiteral().getString();
        }
    }

    public PropertyGraph run(String inputFileName) {
        PropertyGraph pg = new PropertyGraph();
        try {
            InputStream in = FileManager.get().open(inputFileName);
            Reader1 reader = new Reader1();
            RDFDataMgr.parse(reader, in, Lang.TTL);
            pg = reader.getPG();
            if (in == null) {
                throw new IllegalArgumentException("File not found");
            }
        } catch (Exception ex) {
            System.out.println("Error SimpleMapping.run():" + ex.getMessage());
        }        
        return pg;
    }
}
