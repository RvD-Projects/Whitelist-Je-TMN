cls
@echo off
set JAVA18_FOLDER=C:/Program Files/Java/jdk-17/
set DEBUG_ARGS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
set MC_SERVER_FILE=paper.jar
set BOOT_MAX_RAM=4G
set EXEC_MAX_RAM=4G

:begin
set xms = -Xms%BOOT_MAX_RAM%
set xms = -Xms%EXCEC_MAX_RAM%
java -version
java %DEBUG_ARGS% %xms% %xmx% -jar "%MC_SERVER_FILE%" -nogui
echo closing script...
timeout 10

