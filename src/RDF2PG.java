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
import maps.complete.CompleteMapping;
import maps.generic.GenericMapping;
import maps.simple.SimpleMapping;
import pgraph.PropertyGraph;

public class RDF2PG {

    public static void main(String[] args) {
        long itime;
        long etime;
        //BasicConfigurator.configure(); //to avoid log4j warning
        System.out.println("rdf2pg");
        System.out.println("Java app to transform an RDF database into a Property Graph database (i.e. schema and instance data).");
        if (args.length == 2) {
            String opt = String.valueOf(args[0]);
            String input_filename = String.valueOf(args[1]);
            itime = System.currentTimeMillis();
            if (opt.compareTo("-sdm") == 0) {
                System.out.println("Running Simple Database Mapping");
                SimpleMapping smap = new SimpleMapping();
                smap.run(input_filename);
                System.out.println("Output: instance.ypg");
            } else if (opt.compareTo("-gdm") == 0) {
                System.out.println("Running Generic Database Mapping");
                GenericMapping gdm = new GenericMapping();
                gdm.run(input_filename);
                PropertyGraph schema = gdm.getPGSchema();
                schema.exportAsYPG("schema.ypg");
                System.out.println("Output: instance.ypg and schema.ypg");
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
            if (opt.compareTo("-cdm") == 0) {
                System.out.println("Running Complete Database Mapping");
                CompleteMapping cdm = new CompleteMapping();
                cdm.run(rdf_filename, rdfs_filename);
                System.out.println("Output: instance.ypg and schema.ypg");
            } else {
                System.out.println("Invalid option");
            }
            etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");

        } else {
            System.out.println("Usage:");
            System.out.println("// Simple instance mapping");
            System.out.println("$ java -jar rdf2pg -sdm <RDF_filename>");
            System.out.println("// General database mapping (schema-independent)");
            System.out.println("$ java -jar rdf2pg -gdm <RDF_filename>");
            System.out.println("// Direct database mapping (schema-dependent)");
            System.out.println("$ java -jar rdf2pg -cdm <RDF_filename> <RDFS_filename>");
            return;
        }
    }

}
