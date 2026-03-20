@echo off
echo Copying image assets...
copy /Y "AircraftWar-base\src\images\*" "app\src\main\assets\images\"
echo Copying sound assets...
copy /Y "AircraftWar-base\src\videos\*" "app\src\main\assets\sounds\"
echo Done!
pause

