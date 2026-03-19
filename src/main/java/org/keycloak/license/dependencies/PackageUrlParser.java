package org.keycloak.license.dependencies;

import org.jboss.logging.Logger;
import org.keycloak.license.report.beans.ReportBean;

public class PackageUrlParser {
    private static final Logger LOGGER = Logger.getLogger(PackageUrlParser.class);

    public static String toUrl(String pUrl) {
        try {
            String[] s = pUrl.substring(0, pUrl.indexOf('@')).split("/");
            if (s[0].equals("pkg:maven")) {
                return "https://central.sonatype.com/artifact/" + s[1] + "/" + s[2];
            } else if (s[0].equals("pkg:npm")) {
                if (s.length == 2) {
                    return "https://www.npmjs.com/package/" + s[1];
                } else {
                    return "https://www.npmjs.com/package/" + s[1] + "/" + s[2];
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse url: " + pUrl);
            return null;
        }
    }

}
