#!/bin/bash
# --------------------------------------------------------------------------------
# This script can be used to double check that an encrypted variable found in a
# file (e.g. gpg key id) is correct (e.g. no misspellings have occurred).
# It is the peer of 'release_util_script_encrypt_var.sh' - which is used for
# encrypting a variable (e.g. a gpg key id).
# --------------------------------------------------------------------------------
set -euo pipefail

SSL_PWD="$1"
VAR_ENC="$2"

echo "decrypted var is:"
VAR=$(echo "${VAR_ENC}" | openssl enc -A -base64 -d | openssl aes-256-cbc -d -a -pass pass:"$SSL_PWD")
echo "${VAR}"
