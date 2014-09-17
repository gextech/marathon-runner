#!/bin/bash

marathon(){

  FILE=cli/build/libs/cli-0.1.0-SNAPSHOT-all.jar

  if [ ! -f "$FILE" ]
  then
    ./gradlew clean shadowJar
  fi

  #exec java -jar "$FILE" "$@"
}


marathon ls default >/dev/null && marathon use default >/dev/null || true
