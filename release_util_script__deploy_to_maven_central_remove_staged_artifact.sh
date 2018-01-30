#!/bin/bash
set -euo pipefail

SSL_PWD=$1
openssl enc -aes-256-cbc -d -pass pass:"${SSL_PWD}" -in mvnsettingsPlainText.xml.enc -out mvnsettingsPlainText.xml

mvn nexus-staging:drop -DstagingDescription="No longer required" --settings mvnsettingsPlainText.xml
rm mvnsettingsPlainText.xml
