
import maps.complete.CompleteMapping;
import maps.generic.GenericMapping;
import maps.simple.SimpleMapping;

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
/**
 *
 * Class to test the result of the simple instance mapping with Neo4j
 */
public class Neo4jTester {

    public static void main(String[] args) {
        
        
        System.out.println("Simple database mapping");
        SimpleMapping sim = new SimpleMapping();
        Neo4jWriter pgwriter = new Neo4jWriter("instance-sdm.txt");
        sim.run("instance.nt", pgwriter);
                
        System.out.println("Generic database mapping");
        Neo4jWriter instance_pgwriter = new Neo4jWriter("instance-gdm.txt");
        Neo4jWriter schema_pgwriter = new Neo4jWriter("schema-gdm.txt");
        GenericMapping gdm = new GenericMapping();
        gdm.run("instance.nt",instance_pgwriter,schema_pgwriter);
        
        System.out.println("Complete database mapping");
        Neo4jWriter instance_pgwriter2 = new Neo4jWriter("instance-gdm.txt");
        Neo4jWriter schema_pgwriter2 = new Neo4jWriter("schema-gdm.txt");
        CompleteMapping cdm = new CompleteMapping();
        cdm.run("instance.nt", "schema.ttl",instance_pgwriter2,schema_pgwriter2);

    }

}
