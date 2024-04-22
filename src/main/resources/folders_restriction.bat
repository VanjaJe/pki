@echo off

set "folder1Path=C:\Users\Sonja\Desktop\pki\src\main\resources\keys"
set "folder2Path=C:\Users\Sonja\Desktop\pki\src\main\resources\keystore"
set "txtFilePath=C:\Users\Sonja\Desktop\pki\src\main\resources\password.txt"

icacls.exe "%folder1Path%" /inheritance:r
icacls.exe "%folder1Path%" /grant:r Administrators:(OI)(CI)F /T
icacls.exe "%folder1Path%" /remove:g Users /T

icacls.exe "%folder2Path%" /inheritance:r
icacls.exe "%folder2Path%" /grant:r Administrators:(OI)(CI)F /T
icacls.exe "%folder2Path%" /remove:g Users /T

icacls.exe "%txtFilePath%" /inheritance:r
icacls.exe "%txtFilePath%" /grant:r Administrators:(OI)(CI)F /T
icacls.exe "%txtFilePath%" /remove:g Users /T

pause