@echo off
chcp 65001 >nul
echo Запуск Flower Shop...
echo.

REM Простой запуск без проверок
mvn javafx:run

pause

