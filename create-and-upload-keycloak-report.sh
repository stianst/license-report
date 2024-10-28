#!/bin/bash -e

./create-keycloak-report.sh $1
gh release -R keycloak/keycloak upload --clobber $1 third-party-notice-keycloak.html
