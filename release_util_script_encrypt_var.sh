#!/bin/bash
set -euo pipefail

SSL_PWD="$1"
VAR="$2"

echo "encrypted var is:"
VAR_ENC=$(echo "${VAR}" | openssl aes-256-cbc -a -salt -pass pass:"${SSL_PWD}" | openssl enc -A -base64)
echo "${VAR_ENC}"
