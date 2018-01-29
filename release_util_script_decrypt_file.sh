#!/bin/bash
set -euo pipefail

SSL_PWD="$1"
FILE="$2"

openssl enc -aes-256-cbc -salt -pass pass:"${SSL_PWD}" -d -in ${FILE} -out ${FILE}.decrypted
