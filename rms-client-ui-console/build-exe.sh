#!/bin/bash
if [ -d ./RmsConsole ]; then
  rm -rf ./RmsConsole
fi

mvn -Pcli,copy-libs,local,product clean package -DskipTests=true --file pom.xml
cp ./target/rms-client-ui-console.jar ./target/libs

JAVA_HOME=/C/Java/jdk-16.0.1+9
APP_NAME=RmsConsole
MAIN_JAR=rms-client-ui-console.jar
BUIDL_TYPE=app-image
JAVA_OPTION="-DconfiguredCdi.register.0.alias=local  -Drms.h2.script=classpath:init-rms-demo.ddl"

$JAVA_HOME/bin/jpackage --name $APP_NAME --input target/libs --main-jar $MAIN_JAR --type $BUIDL_TYPE --java-options "$JAVA_OPTION" --icon ./env/profile/product/app.ico
