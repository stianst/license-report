#!/bin/bash -e

for i in $(gh release list -R keycloak/keycloak -L 100 | grep ^20. | cut -f 1); do
    echo $i
    ./create-keycloak-report.sh $i
    gh release -R keycloak/keycloak upload --clobber $i third-party-notice-keycloak.html
done