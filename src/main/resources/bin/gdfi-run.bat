@ECHO OFF

rem Which java to use
IF ["%JAVA_HOME%"] EQU [""] (
	set JAVA=java
) ELSE (
	set JAVA="%JAVA_HOME%/bin/java"
)

set FILE_PATH=$1
cd %~dp0../lib
%JAVA% -Xms256M -Xmx1024M -jar -Dfile.location.input=%FILE_PATH% call-report-service.jar