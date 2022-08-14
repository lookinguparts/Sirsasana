mvn install:install-file -Dfile=classpath/core.jar -DgroupId=org.processing -DartifactId=core -Dversion=4.0b8 -Dpackaging=jar
mvn install:install-file -Dfile=classpath/jogl-all.jar -DgroupId=org.jogamp.jogl -DartifactId=jogl-all -Dversion=4.0b8 -Dpackaging=jar
mvn install:install-file -Dfile=classpath/gluegen-rt.jar -DgroupId=org.jogamp.gluegen -DartifactId=gluegen-rt-main -Dversion=4.0b8 -Dpackaging=jar
