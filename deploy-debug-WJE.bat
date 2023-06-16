set SERVER="V:\Serveur Test IDE\1 Skywars"
set BUILDER="C:\Users\Utilisateur\Desktop\Whitelist-je\15"
set PLUGIN_FILTER=Whitelist-Je*shaded.jar


cd %BUILDER%
XCOPY target\%PLUGIN_FILTER% %SERVER%\plugins\ /S /Y

cd %SERVER%
START "Minecraft-Debug" launch_debug.bat

echo Job's done SERVER is awaiting debug socket connection...
