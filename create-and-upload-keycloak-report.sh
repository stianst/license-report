#!/bin/bash -e

./create-keycloak-report.sh $i
gh release -R keycloak/keycloak upload --clobber $i third-party-notice-keycloak.html
