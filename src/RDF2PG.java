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
import maps.DatabaseMapping;
import maps.GeneralDatabaseMapping;
import maps.SimpleMapping;
import pgraph.PropertyGraph;

public class RDF2PG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long itime;
        long etime;
        //BasicConfigurator.configure(); //to avoid log4j warning
        if (args.length == 2) {
            String opt = String.valueOf(args[0]);
            String input_filename = String.valueOf(args[1]);
            itime = System.currentTimeMillis();
            if (opt.compareTo("-sim") == 0) {
                System.out.println("Running Simple Mapping");
                SimpleMapping smap = new SimpleMapping();
                PropertyGraph pg = smap.run(input_filename);
                pg.exportAsYPG("instance-output.pgf");
                System.out.println("Output: instance-output.pgf");
            } else if (opt.compareTo("-gdm") == 0) {
                System.out.println("Running General Database Mapping");
                GeneralDatabaseMapping gdm = new GeneralDatabaseMapping();
                gdm.run(input_filename);
                PropertyGraph instance = gdm.getPGInstance();
                instance.exportAsYPG("instance-output.pgf");
                PropertyGraph schema = gdm.getPGSchema();
                schema.exportAsYPG("schema-output.pgf");
                System.out.println("Output: instance-output.pgf and schema-output.pgf");
            } else if (opt.compareTo("-dsm") == 0) {
                System.out.println("Running Direct Schema Mapping");
                //DatabaseMapping dbm = new DatabaseMapping();
                //PropertyGraph pg = dbm.runSchemaMapping(input_filename);
                //pg.exportAsYPG("schema-output.pgf");
                System.out.println("Output: schema-output.pgf");
            } else if (opt.compareTo("-dim") == 0) {
                System.out.println("Running Direct Instance Mapping");
                //DatabaseMapping dbm = new DatabaseMapping();
                //PropertyGraph pg = dbm.runInstanceMapping(input_filename);
                //pg.exportAsYPG("instance-output.pgf");
                System.out.println("instance-output.pgf");
            } else {
                System.out.println("Invalid option");
            }
            etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");

        } else if (args.length == 3) {
            itime = System.currentTimeMillis();
            String opt = String.valueOf(args[0]);
            String RDF_filename = String.valueOf(args[1]);
            String RDFS_filename = String.valueOf(args[2]);
            if (opt.compareTo("-ddm") == 0) {
                System.out.println("Running Direct Database Mapping");
                DatabaseMapping dbm = new DatabaseMapping();
                dbm.run(RDF_filename, RDFS_filename);
                PropertyGraph pg_schema = dbm.getPGSchema();
                pg_schema.exportAsYPG("schema-output.pgf");
                PropertyGraph pg_instance = dbm.getPGInstance();
                pg_instance.exportAsYPG("instance-output.pgf");
                System.out.println("Output: instance-output.pgf and schema-output.pgf");
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
