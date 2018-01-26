#!/bin/bash
#------------------------------------------------------------
## @todo - test with gpg key with no passphrase so key provided by peter schrammel can be used

## note - as a file with plain text is generated - it would be best if these were placed in a location
## external to the repository, so they can't be checked in accidentally. Travis would also be good - all data is volatile.

#------------------------------------------------------------
#VERSION="0.0.1-SNAPSHOT"
VERSION="0.0.1"
RELEASE="true"
#------------------------------------------------------------
SSL_PWD="$1"
## encoding command used of form "echo "$var" | openssl aes-256-cbc -a -salt -pass pass:${SSL_PWD} | openssl enc -A -base64" 
SONATYPETOKEN_USER_ENC="VTJGc2RHVmtYMThEV2pndXpiK3lFMmQyRDgvTmhBUllTWldac3NITDMxQT0K"
SONATYPETOKEN_PWD_ENC="VTJGc2RHVmtYMS8vam9YR0U4dExXbmlwaC9JR094TGJvTlN2ajVhOHc4bjZOL1I1UEZXY05mWFU4N2NHdzc5WgpsWXpITkIzU3pLZ2lkY2MrQkZPMDZRPT0K"
GPG_KEYID_ENC="VTJGc2RHVmtYMTljcG9SQW13SVcwMHJlaVFIRUxQWkJaSVFKTDRIelB6OU5JVWNRMU1DbHN0Qmg2OHhyL29tVgpmSkpoQUhwR1pSWWUxVDVlbGtaajdnPT0K"
GPG_PWD_ENC="VTJGc2RHVmtYMS9RallKTWJBRVFWTk41VnN0UUNWZHNxbkZNNnFVclZlYjVWYjJldDhFK0cyc2Irc2l1NWc2Ywo="

decrypt_fn(){
    echo "$1" | openssl enc -A -base64 -d | openssl aes-256-cbc -d -a -pass pass:"$SSL_PWD"
}

export SONATYPETOKENUSER=`decrypt_fn "${SONATYPETOKEN_USER_ENC}"`
export SONATYPETOKENPWD=`decrypt_fn "${SONATYPETOKEN_PWD_ENC}"`
export GPG_KEYID=`decrypt_fn "${GPG_KEYID_ENC}"`
export GPG_PWD=`decrypt_fn "${GPG_PWD_ENC}"`

#------------------------------------------------------------
## nb can generate a disposable gpg key (& revoke certificate) on the fly to sign the artfacts if so desired.
## atm reusing a key registered to 'sonny.martin@diffblue.com' - expiry 23 feb 2018 (1 month)
openssl enc -aes-256-cbc -d -pass pass:"${SSL_PWD}" -in private.gpg.enc -out private.gpg
gpg --fast-import private.gpg
gpg --keyserver pgp.mit.edu --send-keys ${GPG_KEYID} #in case the key has not already been registered with a public keyserver (it has)

## encoding command is of form 'openssl enc -aes-256-cbc -salt -in file.txt -out file.txt.enc' - nb 'salt' 
openssl enc -aes-256-cbc -d -pass pass:"${SSL_PWD}" -in mvnsettingsPlainText.xml.enc -out mvnsettingsPlainText.xml

#------------------------------------------------------------
if [[ "${RELEASE}" == "false" ]]
then
    echo "this is a 'snapshot' release of deeptest-utils.jar, version is: '${VERSION}' "
    # mvn clean install -DskipTests=true -B -V
else
    echo "this is a 'release' version of deeptest-utils.jar, version is: '${VERSION}' "
    # mvn clean deploy -DskipTests=true -P sign,build-extras,stdbuild --settings mvnsettingsUseEnvVars.xml -B -V ##maven fails to replace in settings.xml
    mvn clean deploy -DskipTests=true -P sign,build-extras,stdbuild --settings mvnsettingsPlainText.xml -B -V
fi
#------------------------------------------------------------
rm private.gpg
## remove key from keyring, if this was a gpg key generated on the fly - then it would be gone forever.
gpg --batch --delete-secret-keys ${GPG_KEYID}
gpg --batch --delete-key ${GPG_KEYID}

rm mvnsettingsPlainText.xml
#------------------------------------------------------------
