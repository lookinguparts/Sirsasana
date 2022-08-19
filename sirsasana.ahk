#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.

; #Warn  ; Enable warnings to assist with detecting common errors.

SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.

SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.

; SetTimer, CheckForExe, 5000 ;frequency

Loop
{
    ; Wait 35 seconds.  SuperCollider is 30 seconds so hopefully this will help SuperCollider
    ; start first so it doesn't miss the first playing.
    Sleep 35000  ; Sleep first to avoid machine startup timing issues.

    Process, Exist, java.exe
    
    if not Errorlevel {    
	Run, C:\Users\accou\Documents\GitHub\Sirsasana\sirsasana.bat, C:\Users\accou\Documents\GitHub\Sirsasana
	  Sleep 35000
	}
}
