package org.apache.roller.weblogger.planet.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.roller.weblogger.config.runtime.RuntimeConfigDefs;
import org.apache.roller.weblogger.config.runtime.RuntimeConfigDefsParser;

import java.io.InputStream;

public class PlanetRuntimeConfigDef {
    private static RuntimeConfigDefs configDefs = null;
    private static final String runtimeConfig = "/org/apache/roller/planet/config/planetRuntimeConfigDefs.xml";
    private static final Log log = LogFactory.getLog(PlanetRuntimeConfigDef.class);
    public static RuntimeConfigDefs getRuntimeConfigDefs() {

        if(configDefs == null) {

            // unmarshall the config defs file
            try {
                InputStream is =
                        PlanetRuntimeConfigDef.class.getResourceAsStream(runtimeConfig);

                RuntimeConfigDefsParser parser = new RuntimeConfigDefsParser();
                configDefs = parser.unmarshall(is);

            } catch(Exception e) {
                // error while parsing :(
                log.error("Error parsing runtime config defs", e);
            }

        }

        return configDefs;
    }
}
