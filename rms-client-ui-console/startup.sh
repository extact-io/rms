#!/bin/bash
#-----------------------
# import function
#-----------------------
source ../def_function.sh

#-----------------------
# execute cases
#-----------------------
case "$1" in
  "test")
    maven_build_test
    ;;
  "build")
    maven_build
    ;;
  "java")
    test 1 -eq 1
    ;;
  "help")
    echo "test  -> maven_build_test->execute java"
    echo "build -> maven_build->execute java"
    echo "java  -> execute java"
    exit 0
    ;;
  *)
    maven_build
    ;;
esac

if [ $? -eq 0 ]; then
  exec_java rms-client-ui-console.jar
fi

exit
