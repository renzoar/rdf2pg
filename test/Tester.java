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

import maps.DirectMapping;
import maps.GeneralMapping;
import maps.SimpleMapping;
import pgraph.PropertyGraph;

public class Tester {

    public static void main(String[] args) {
        // TODO code application logic here

        /*
        System.out.println("Simple instance mapping");
        PropertyGraph pg;
        SimpleMapping sim = new SimpleMapping();
        pg = sim.run("instance.ttl");
        pg.exportAsYPG("./temp/sim.pgf");
        */

        /*
        System.out.println("General Database Mapping");
        GeneralDatabaseMapping gdm = new GeneralDatabaseMapping();
        gdm.run("instance.ttl");
        PropertyGraph instance = gdm.getPGInstance();
        instance.exportAsYPG("./temp/gdm-instance.pgf");
        PropertyGraph schema = gdm.getPGSchema();
        schema.exportAsYPG("./temp/gdm-schema.pgf");
        */
                
        System.out.println("Database Mapping");
        DirectMapping dbm = new DirectMapping();
        dbm.run("instance.ttl", "schema.ttl");
        PropertyGraph pgi = dbm.getPGInstance();
        pgi.exportAsYPG("/tmp/ddm-instance.pgf");
        PropertyGraph pgs = dbm.getPGSchema();
        pgs.exportAsYPG("/tmp/ddm-schema.pgf");

        System.out.println("OK");
    }

}
