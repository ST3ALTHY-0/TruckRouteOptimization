@echo off
setlocal

set "ORS_DIR=C:\Programming\DockerStuffs\openRouteService"

if not exist "%ORS_DIR%" (
	echo Error: ORS directory not found: "%ORS_DIR%"
	exit /b 1
)

pushd "%ORS_DIR%" || exit /b 1
docker compose up -d
set "EXIT_CODE=%ERRORLEVEL%"
popd

exit /b %EXIT_CODE%