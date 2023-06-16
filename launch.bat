cls
@echo off
set JAVA18_FOLDER=C:/Program Files/Java/jdk-17/
set MC_SERVER_FILE=paper.jar
set BOOT_MAX_RAM=4G
set EXEC_MAX_RAM=4G

:begin
set xms = -Xms%BOOT_MAX_RAM%
set xms = -Xms%EXCEC_MAX_RAM%
java -version
java %xms% %xmx% -jar "%MC_SERVER_FILE%" -nogui
echo closing script...
timeout 10

