#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
; SetTimer, CheckForExe, 5000 ;frequency

Loop
{
    Sleep 30000  ; Sleep first to avoid machine startup timing issues.

    Process, Exist, sclang.exe

    if not Errorlevel {
	Run, C:\Users\Tracy\Documents\GitHub\sirsasana\sc\sirsynthd.bat, C:\Users\Tracy\Documents\GitHub\sirsasana\sc
	  Sleep 30000
	}
}
