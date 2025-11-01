@echo off
REM Change directory to project src
cd src

REM Compile all Java files (using all jars in lib folder)
javac -cp ".;../lib/*" *.java

REM Run the LoginForm
java -cp ".;../lib/*" LoginForm

pause
