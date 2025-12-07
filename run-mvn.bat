@echo off
setlocal enabledelayedexpansion
set PATH=C:\Maven\apache-maven-3.9.6\bin;%PATH%
cd /d C:\Users\USER\IdeaProjects\flowerapps
mvn javafx:run
pause
