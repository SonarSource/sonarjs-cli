#!/bin/bash
set -euo pipefail

CURRENT_VERSION=`mvn help:evaluate -Dexpression="project.version" | grep -v '^\[\|Download\w\+\:'`

if [[ $CURRENT_VERSION =~ "-SNAPSHOT" ]]; then
  echo "Running locally"
else
  echo "Running for $CI_BUILD_NUMBER"
  #deploy the version built by travis
  mkdir -p target
  cd target
  curl --user $ARTIFACTORY_QA_READER_USERNAME:$ARTIFACTORY_QA_READER_PASSWORD -sSLO https://repox.sonarsource.com/sonarsource-public-qa/org/sonarsource/sonarjs/cli/sonarjs-cli/$CURRENT_VERSION/sonarjs-cli-$CURRENT_VERSION.zip
  cd ..
  mvn install:install-file -Dfile=target/sonarjs-cli-$CURRENT_VERSION.zip -DgroupId=org.sonarsource.sonarjs.cli \
    -DartifactId=sonarjs-cli -Dversion=$CURRENT_VERSION -Dpackaging=zip
fi

# ADD ITs when and if they will exist
