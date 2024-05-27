# License report tool for Keycloak

## Server distribution

### Checkout the tag

```
git clone git@github.com:keycloak/keycloak.git
cd keycloak
git checkout <VERSION>
```

### Create SBOM for Maven

```
cd quarkus/deployment
mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom -Dcyclonedx.skipAttach=true
```

Path to the generated file is `quarkus/deployment/target/bom.json`.

### Create SBOM for PNPM

Install `https://github.com/CycloneDX/cdxgen`:

```
sudo npm install -g @cyclonedx/cdxgen
```

```
FETCH_LICENSE=true cdxgen -t pnpm --required-only --no-babel
```

Path to the generated file is `bom.json`.

## Create dependency report

```
java -jar target/license-report.jar -p server report --sbom <KEYCLOAK CHECKOUT>/bom.json --sbom <KEYCLOAK CHECKOUT>/quarkus/deployment/target/bom.json
```



## Operator distribution

### Checkout the tag

```
git clone git@github.com:keycloak/keycloak.git
cd keycloak
git checkout <VERSION>
```

### Create SBOM for Maven

Generate CycloneDX SBOM for Operator:
```
cd operator
mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom -Dcyclonedx.skipAttach=true
```

## Create dependency report

```
java -jar target/license-report.jar -p operator report --sbom <KEYCLOAK CHECKOUT>/operator/target/bom.json
```



Path to the generated file is `operator/target/bom.json`.
