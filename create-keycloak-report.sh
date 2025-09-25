#!/bin/bash -e

rm -f *.html

VERSION=$1

if [ "$VERSION" == "" ]; then
    echo "usage: create-keycloak-server-report.sh <version>"
    exit 1
fi

mkdir -p cache/repositories
cd cache/repositories

if [ ! -d keycloak ]; then
    git clone https://github.com/keycloak/keycloak.git
    cd keycloak
else
    cd keycloak
    git fetch origin --tags -f
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


cd ../../../

echo "=============================================================================="
echo "Creating report for Keycloak distribution"
echo "------------------------------------------------------------------------------"

java -jar target/license-report.jar -p keycloak report --ignore-validation --sbom cache/repositories/keycloak/bom.json --sbom cache/repositories/keycloak/quarkus/deployment/target/bom.json --sbom cache/repositories/keycloak/operator/target/bom.json
