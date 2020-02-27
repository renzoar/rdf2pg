
import maps.DirectMapping;
import maps.GeneralMapping;
import maps.SimpleMapping;
import pgraph.PropertyGraph;

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

public class Neo4jTest {
    
    public static void main(String[] args) {
        long itime;
        long etime;
        //BasicConfigurator.configure(); //to avoid log4j warning
        System.out.println("rdf2pg (for Neo4j)");
        System.out.println("Java app to transform an RDF database into a Property Graph database (i.e. schema and instance data).");
        if (args.length == 2) {
            String opt = String.valueOf(args[0]);
            String input_filename = String.valueOf(args[1]);
            itime = System.currentTimeMillis();
            if (opt.compareTo("-sim") == 0) {
                System.out.println("Running Simple Mapping");
                SimpleMapping smap = new SimpleMapping();
                PropertyGraph pg = smap.run(input_filename);
                pg.exportAsCypher("instance.txt");
                System.out.println("Output: instance.txt");
            } else if (opt.compareTo("-gdm") == 0) {
                System.out.println("Running General Database Mapping");
                GeneralMapping gdm = new GeneralMapping();
                gdm.run(input_filename);
                PropertyGraph instance = gdm.getPGInstance();
                instance.exportAsCypher("instance.txt");
                PropertyGraph schema = gdm.getPGSchema();
                schema.exportAsCypher("schema.txt");
                System.out.println("Output: instance.txt and schema.txt");
            } else if (opt.compareTo("-dsm") == 0) {
                System.out.println("Running Direct Schema Mapping");
                DirectMapping dbm = new DirectMapping();
                PropertyGraph pg = dbm.runSchemaMapping(input_filename);
                pg.exportAsCypher("schema.txt");
                System.out.println("Output: schema.txt");
            } else if (opt.compareTo("-dim") == 0) {
                System.out.println("Running Direct Instance Mapping");
                DirectMapping dbm = new DirectMapping();
                PropertyGraph pg = dbm.runInstanceMapping(input_filename);
                pg.exportAsCypher("instance.txt");
                System.out.println("Output: instance.txt");
            } else {
                System.out.println("Invalid option");
            }
            etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");

        } else if (args.length == 3) {
            itime = System.currentTimeMillis();
            String opt = String.valueOf(args[0]);
            String rdf_filename = String.valueOf(args[1]);
            String rdfs_filename = String.valueOf(args[2]);
            if (opt.compareTo("-ddm") == 0) {
                System.out.println("Running Direct Database Mapping");
                DirectMapping dbm = new DirectMapping();
                dbm.run(rdf_filename, rdfs_filename);
                PropertyGraph pg_schema = dbm.getPGSchema();
                pg_schema.exportAsCypher("schema.txt");
                PropertyGraph pg_instance = dbm.getPGInstance();
                pg_instance.exportAsCypher("instance.txt");
                System.out.println("Output: instance.txt and schema.txt");
            } else {
                System.out.println("Invalid option");
            }
            etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");

        } else {
            System.out.println("Usage:");
            System.out.println("// Simple instance mapping");
            System.out.println("$ java -jar rdf2pg -sim <RDF_filename>");
            System.out.println("// General database mapping (schema-independent)");
            System.out.println("$ java -jar rdf2pg -gdm <RDF_filename>");
            System.out.println("// Direct database mapping (schema-dependent)");
            System.out.println("$ java -jar rdf2pg -ddm <RDF_filename> <RDFS_filename>");
            System.out.println("// Direct instance mapping");
            System.out.println("$ java -jar rdf2pg -dim <RDF_filename>");
            System.out.println("// Direct schema mapping");
            System.out.println("$ java -jar rdf2pg -dsm <RDFS_filename>");
            return;
        }
    }
    
    
}
