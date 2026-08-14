@echo off
REM =========================================================================
REM  CinemaBookingApp - Windows .exe installer бэлдэх скрипт
REM  Шаардлага:
REM    1) JDK 17+ (jpackage-тай хамт ирдэг)  -> PATH дээр байх ёстой
REM    2) Maven                              -> PATH дээр байх ёстой
REM    3) JavaFX SDK 21 (доор татаж авах холбоос)
REM       https://gluonhq.com/products/javafx/  ->  "SDK" хувилбарыг татна
REM       Жишээ зам: C:\javafx-sdk-21.0.2
REM  Ажиллуулах: build-installer.bat "C:\javafx-sdk-21.0.2"
REM =========================================================================

setlocal

if "%~1"=="" (
    echo Ашиглах заавар: build-installer.bat "C:\javafx-sdk-21.0.2\lib"
    echo JavaFX SDK-ийн lib хавтасны замыг оруулна уу.
    exit /b 1
)

set JFX_LIB=%~1

echo [1/3] Maven-ээр fat-jar угсарч байна...
call mvn clean package
if errorlevel 1 (
    echo Maven build амжилтгүй боллоо.
    exit /b 1
)

echo [2/3] Хуучин installer хавтсыг цэвэрлэж байна...
if exist installer rmdir /s /q installer
mkdir installer

echo [3/3] jpackage-ээр .exe installer үүсгэж байна...
jpackage ^
  --type exe ^
  --name "CinemaBooking" ^
  --app-version "1.0.0" ^
  --vendor "CinemaBooking" ^
  --input target ^
  --dest installer ^
  --main-jar CinemaBookingApp-1.0.0.jar ^
  --main-class mn.cinema.Main ^
  --icon src\main\resources\mn\cinema\images\icon.ico ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser ^
  --java-options "--module-path \"%JFX_LIB%\" --add-modules javafx.controls,javafx.fxml"

if errorlevel 1 (
    echo jpackage амжилтгүй боллоо. JDK, JavaFX SDK замуудаа шалгана уу.
    exit /b 1
)

echo.
echo Амжилттай! installer\ хавтас дотор CinemaBooking-1.0.0.exe үүслээ.
endlocal
