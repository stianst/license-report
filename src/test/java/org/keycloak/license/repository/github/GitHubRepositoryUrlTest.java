package org.keycloak.license.repository.github;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GitHubRepositoryUrlTest {

    @Test
    public void testRepositoryUrl() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("https://github.com/owner/repository");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/owner/repository/", url.getSourceUrl());
    }

    @Test
    public void testDirectoryUrl() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("https://github.com/owner/repository/some/dir");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/owner/repository/", url.getSourceUrl());
    }

    @Test
    public void testNotGitHub() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("http://nexus.sonatype.org/oss-repository-hosting.html/vertx-parent/vertx-core");
        Assertions.assertNull(url);
    }

    @Test
    public void scmLink() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("scm:git:git://github.com/owner/repository.git");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/owner/repository/", url.getSourceUrl());
    }

    @Test
    public void scmLink2() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("scm:git:git@github.com:owner/repository.git");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/owner/repository/", url.getSourceUrl());
    }

    @Test
    public void scmLinkHttp() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("git+https://github.com/typescript-eslint/typescript-eslint.git");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/typescript-eslint/typescript-eslint/", url.getSourceUrl());
    }

    @Test
    public void scmLinkHttp2() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("git+https://github.com/ast-grep/ast-grep.git");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/ast-grep/ast-grep/", url.getSourceUrl());
    }

    @Test
    public void stripsFragment() {
        GitHubRepositoryUrl url = GitHubRepositoryUrl.parse("https://github.com/4teamwork/cypress-drag-drop#readme");
        Assertions.assertNotNull(url);
        Assertions.assertEquals("https://github.com/4teamwork/cypress-drag-drop/", url.getSourceUrl());
    }

}
