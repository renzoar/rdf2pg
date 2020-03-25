
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
        SimpleMapping sim = new SimpleMapping();
        Neo4jWriter pgwriter = new Neo4jWriter("instance.txt");
        sim.run("instance.nt", pgwriter);
    }

}
