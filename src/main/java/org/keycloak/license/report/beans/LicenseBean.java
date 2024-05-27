package org.keycloak.license.report.beans;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.keycloak.license.licenses.spdx.License;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
public class LicenseBean {

    private final License spdxLicense;

    private List<DependencyBean> dependencies = new LinkedList<>();

    public LicenseBean(License spdxLicense) {
        this.spdxLicense = spdxLicense;
    }

    public String getId() {
        return spdxLicense.getLicenseId();
    }

    public License getSpdxLicense() {
        return spdxLicense;
    }

    public List<DependencyBean> getDependencies() {
        return dependencies.stream().sorted(Comparator.comparing(DependencyBean::getReference)).toList();
    }

    public void add(DependencyBean dependencyBean) {
        this.dependencies.add(dependencyBean);
    }

}
