#!/bin/bash

changelog_version=$(head -n 1 CHANGELOG.md)
maven_version=$(mvn help:evaluate -Dexpression=project.version | grep -v -e "^\\[")

if [ "$maven_version" == "$changelog_version" ]
then
  exit 0
fi

echo Version mismatch CHANGELOG.md: "$changelog_version" vs. Maven: "$maven_version"
exit 1
