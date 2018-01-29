#!/bin/bash
set -eu

SSL_PWD="$1"
VAR_ENC="$2"

echo "decrypted var is:"
VAR=`echo "${VAR_ENC}" | openssl enc -A -base64 -d | openssl aes-256-cbc -d -a -pass pass:"$SSL_PWD"`
echo ${VAR}
