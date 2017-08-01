@ECHO OFF

rem Which java to use
IF ["%JAVA_HOME%"] EQU [""] (
	set JAVA=java
) ELSE (
	set JAVA="%JAVA_HOME%/bin/java"
)

set FILE_PATH=%1
%JAVA% -Xms256M -Xmx1024M -jar -Dfile.location.input=%FILE_PATH% -Dmode="monitor" ../lib/call-report-service.jar