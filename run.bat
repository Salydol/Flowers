@echo off
chcp 65001 >nul
echo ========================================
echo   Flower Shop Application Launcher
echo ========================================
echo.

REM Проверка наличия Java
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ОШИБКА] Java не найдена в PATH!
    echo Установите Java 17 или выше и добавьте в PATH
    pause
    exit /b 1
)

REM Проверка версии Java
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
    echo Найдена Java: %%g
)
echo.

REM Проверка наличия Maven
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ОШИБКА] Maven не найден в PATH!
    echo Установите Maven и добавьте в PATH
    echo Или используйте IntelliJ IDEA для запуска
    pause
    exit /b 1
)

echo Найден Maven:
mvn --version | findstr "Apache Maven"
echo.

echo ========================================
echo   Проверка перед запуском:
echo ========================================
echo 1. Java 17+ установлена
echo 2. Maven установлен
echo 3. PostgreSQL запущен
echo 4. База данных 'flower_shop' создана
echo 5. Таблицы созданы (database_schema.sql)
echo 6. Пароль БД обновлен в DatabaseConnection.java
echo.
echo Нажмите любую клавишу для запуска...
pause >nul
echo.

echo ========================================
echo   Запуск приложения...
echo ========================================
echo.

REM Запуск через Maven
mvn clean javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ОШИБКА] Не удалось запустить приложение!
    echo.
    echo Возможные причины:
    echo - Maven не может найти зависимости
    echo - Проблемы с подключением к БД
    echo - JavaFX не установлен правильно
    echo.
    echo Попробуйте:
    echo 1. mvn clean install
    echo 2. Запустить через IntelliJ IDEA
    echo.
)

pause
