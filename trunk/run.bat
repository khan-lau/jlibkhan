@echo off

rem dir %JAVA_HOME%\lib\*.jar .\*.jar /b /s
rem echo %jarfiles%


java -Djava.util.logging.config.file=D:\Project\ejetcbs\src\CBE\conf\logging.properties -classpath "D:\Develop\Java\jdk1.5.0_02\lib\dt.jar;D:\Develop\Java\jdk1.5.0_02\lib\htmlconverter.jar;D:\Develop\Java\jdk1.5.0_02\lib\jconsole.jar;D:\Develop\Java\jdk1.5.0_02\lib\tools.jar;D:\Project\ejetcbs\src\CBE\dist\CBE.jar;D:\Project\ejetcbs\src\CBE\lib\jetty-6.1.3.jar;D:\Project\ejetcbs\src\CBE\lib\jetty-util-6.1.3.jar;D:\Project\ejetcbs\src\CBE\lib\servlet-api-2.5-6.1.3.jar;D:\Project\ejetcbs\src\CBE\lib\cxf-2.2.jar;D:\Project\ejetcbs\src\CBE\lib\cxf-manifest.jar;D:\Develop\Java\apache-cxf-2.2\lib\cxf-manifest.jar;D:\Project\ejetcbs\src\CBE\lib\geronimo-jaxws_2.1_spec-1.0.jar;D:\Project\ejetcbs\src\CBE\lib\geronimo-stax-api_1.0_spec-1.0.1.jar;D:\Project\ejetcbs\src\CBE\lib\geronimo-ws-metadata_2.0_spec-1.1.2.jar;D:\Project\ejetcbs\src\CBE\lib\jaxb-api-2.1.jar; D:\Project\ejetcbs\src\CBE\lib\jaxb-impl-2.1.9.jar; D:\Project\ejetcbs\src\CBE\lib\wsdl4j-1.6.2.jar;D:\Project\ejetcbs\src\CBE\lib\mysql-connector-java-3.1.14-bin.jar"  com.khan.AppMain.Start 

@echo on