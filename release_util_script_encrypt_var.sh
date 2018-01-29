#!/bin/bash
# --------------------------------------------------------------------------------
# This script can be used to encrypt a variable (e.g. gpg key id, if a key is
# provided by someone else)
# NB release_util_script_create_gpg provides both the key id and also its
# encrypted form.
# This script is the peer of 'release_util_script_decrypt_var.sh' - which can be used to
# double check that an encrypted variable (e.g. gpg key id) is correct (e.g. no
# misspellings have occurred).
# --------------------------------------------------------------------------------
set -euo pipefail

SSL_PWD="$1"
VAR="$2"

echo "encrypted var is:"
VAR_ENC=$(echo "${VAR}" | openssl aes-256-cbc -a -salt -pass pass:"${SSL_PWD}" | openssl enc -A -base64)
echo "${VAR_ENC}"
