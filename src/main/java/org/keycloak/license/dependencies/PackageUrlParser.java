package org.keycloak.license.dependencies;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PackageUrlParser {

    public static String toUrl(String pUrl) {
        if (pUrl == null || pUrl.isEmpty()) return null;
        String decodedPackage = URLDecoder.decode(pUrl, StandardCharsets.UTF_8);
        int firstSlash = decodedPackage.indexOf('/');
        if (firstSlash < 0) return null;
        String packageType = decodedPackage.substring(0, firstSlash);
        String fragment = decodedPackage.substring(firstSlash + 1);
        int params = fragment.indexOf("?");
        fragment = (params < 0 ? fragment : fragment.substring(0, params));
        int lastAtSign = fragment.lastIndexOf("@");
        if (lastAtSign > 0) {
            fragment = fragment.substring(0, lastAtSign);
        }
        if (packageType.equals("pkg:maven")) {
            return "https://central.sonatype.com/artifact/" + fragment;
        } else if (packageType.equals("pkg:npm")) {
            return "https://www.npmjs.com/package/" + fragment;
        } else {
            return null;
        }
    }

}
