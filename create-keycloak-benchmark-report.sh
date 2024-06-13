#!/bin/bash

VERSION=$1

if [ "$VERSION" == "" ]; then
    echo "usage: create-keycloak-benchmark-report.sh <version>"
    exit 1
fi

cd cache

if [ ! -d keycloak-benchmark ]; then
    git clone https://github.com/keycloak/keycloak-benchmark
    cd keycloak-benchmark
else
    cd keycloak-benchmark
    git fetch origin --tags
    git reset --hard origin/main
fi

echo "=============================================================================="
echo "Checkout $VERSION tag"
echo "------------------------------------------------------------------------------"
git checkout $VERSION

echo "=============================================================================="
echo "Generating SBOM for benchmark distribution"
echo "------------------------------------------------------------------------------"
mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom -Dcyclonedx.skipAttach=true -f benchmark/


cd ../../

echo "=============================================================================="
echo "Creating report for benchmark distribution"
echo "------------------------------------------------------------------------------"

java -jar target/license-report.jar -p benchmark report --sbom cache/keycloak-benchmark/benchmark/target/bom.json
