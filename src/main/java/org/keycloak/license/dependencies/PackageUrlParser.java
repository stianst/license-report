package org.keycloak.license.dependencies;

public class PackageUrlParser {

    public static String toUrl(String pUrl) {
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
    }

}
