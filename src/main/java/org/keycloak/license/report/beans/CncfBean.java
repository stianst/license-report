package org.keycloak.license.report.beans;

import org.jboss.logging.Logger;
import org.keycloak.license.config.CncfApproved;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CncfBean {

    private static final Logger LOGGER = Logger.getLogger(ReportBean.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private final ReportBean reportBean;

    public CncfBean(ReportBean reportBean) {
        this.reportBean = reportBean;
    }

    public List<DependencyBean> getUniqueDependencies() {
        Set<String> s = new HashSet<>();
        return reportBean.getLicenses().stream().map(LicenseBean::getDependencies).flatMap(Collection::stream).filter(d -> s.add(d.getGroup() + ":" + d.getName())).sorted(Comparator.comparing(DependencyBean::getReference)).toList();
    }

    public String getUnapprovedLicenseIds() {
        return getUniqueDependencies()
                .stream()
                .filter(d -> !d.isApprovedByCncf())
                .map(d -> d.getAllDeclaredLicenses())
                .flatMap(Collection::stream)
                .map(l -> l.getLicenseId())
                .sorted()
                .distinct().collect(Collectors.joining(", "));
    }

    public String getUnapprovedLicenseNames() {
        return getUniqueDependencies()
                .stream()
                .filter(d -> !d.isApprovedByCncf())
                .map(d -> d.getAllDeclaredLicenses())
                .flatMap(Collection::stream)
                .map(l -> l.getName())
                .sorted()
                .distinct().collect(Collectors.joining(", "));
    }

}
