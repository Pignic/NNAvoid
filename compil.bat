@echo off

set JAVA_HOME=C:\WORKBENCHS\java\jdk1.7.0_79
set M2_HOME=C:\WORKBENCHS\apache-maven-3.3.3
set M2=%M2_HOME%\bin
set PATH=%M2%;%JAVA_HOME%\bin

REM 
call %M2%\mvn clean install package -Dmaven.test.skip=true

pause
@echo on
