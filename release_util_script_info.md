Updating the release key
------------------------

If you change the key (e.g. via `release_util_script_create_gpg.sh`) you need to:
 - get the key id ( `release_util_script_create_gpg.sh` provides this)
 - get the encrypted key id ( `release_util_script_create_gpg.sh` provides this)
 - put the encrypted key id in file
   `release_util_script__deploy_to_maven_central.sh`, in var: `GPG_KEYID_ENC`
 - decode `mvnsettingsPlainText.xml.enc` (use
   `release_util_script_decrypt_file.sh`)
 - put the (non-encrypted) key id in it
 - reencode to ‘mvnsettingsPlainText.xml.enc’ (use
   `release_util_script_encrypt_file.sh`)
 - remove the deencrypted settings file `mvnsettingsPlainText.xml.enc.decrypted`
