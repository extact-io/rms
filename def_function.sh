#-----------------------
# define function
#-----------------------
maven_build() {
  mvn -Peclipse,copy-libs clean package
}
maven_build_test() {
  mvn -Pcli,copy-libs clean package
}
exec_java() {
  java -jar target/$1
}
