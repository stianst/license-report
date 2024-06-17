package org.keycloak.license.repository;

import java.util.List;

public class TagResolver {

    public static final String UNRESOLVABLE = "UNRESOLVABLE";

    private TagResolver() {
    }

    public static String resolveTagPattern(List<String> tags, String version) {
        if (tags.contains(version)) {
            return "${version}";
        } else if (tags.contains("v" + version)) {
            return "v${version}";
        } else if (tags.contains(version + "-RELEASE")) {
            return "${version}-RELEASE";
        } else {
            for (String t : tags) {
                if (t.endsWith(version)) {
                    return t.replace(version, "${version}");
                }
            }
        }
        return UNRESOLVABLE;
    }

    public static String resolveTagUrl(String version, String tagUrl) {
        if (tagUrl == null || tagUrl.equals(UNRESOLVABLE)) {
            return null;
        }

        String versionUnderscore = version.replace('.', '_');

        return tagUrl.replace("${version}", version).replace("${versionUnderscore}", versionUnderscore);
    }
}
