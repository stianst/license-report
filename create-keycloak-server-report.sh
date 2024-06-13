#!/bin/bash

VERSION=$1

if [ "$VERSION" == "" ]; then
    echo "usage: create-keycloak-server-report.sh <version>"
    exit 1
fi

cd cache

if [ ! -d keycloak ]; then
    git clone https://github.com/keycloak/keycloak.git
    cd keycloak
else
    cd keycloak
    git fetch origin --tags
    git reset --hard origin/main
fi

echo "=============================================================================="
echo "Checkout $VERSION tag"
echo "------------------------------------------------------------------------------"
git checkout $VERSION

echo "=============================================================================="
echo "Generating SBOM for NPM dependencies"
echo "------------------------------------------------------------------------------"
FETCH_LICENSE=true cdxgen -t pnpm --required-only --no-babel

echo "=============================================================================="
echo "Generating SBOM for Quarkus distribution"
echo "------------------------------------------------------------------------------"
mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom -Dcyclonedx.skipAttach=true -f quarkus/deployment/

echo "=============================================================================="
echo "Generating SBOM for Operator distribution"
echo "------------------------------------------------------------------------------"
mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom -Dcyclonedx.skipAttach=true -f operator/


cd ../../

echo "=============================================================================="
echo "Creating report for Quarkus distribution"
echo "------------------------------------------------------------------------------"

java -jar target/license-report.jar -p server report --sbom cache/keycloak/bom.json --sbom cache/keycloak/quarkus/deployment/target/bom.json

echo "=============================================================================="
echo "Creating report for Operator distribution"
echo "------------------------------------------------------------------------------"

java -jar target/license-report.jar -p operator report --sbom cache/keycloak/operator/target/bom.json