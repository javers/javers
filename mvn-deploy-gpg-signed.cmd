REM on Windows, launch it from standard command line (don't use sh)

mvn clean deploy -Dgpg.homedir=%APPDATA%/gnupg -Psign-artifacts