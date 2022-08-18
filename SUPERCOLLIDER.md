SuperCollider Notes

SuperCollider is having issues exiting on Windows.  There is an /exit OSC endpoint I added but calling 0.exit
just hangs.  It is probably some leaked thread bug which seems to periodically pop up in old SuperCollider
issues.  The only way to kill it is to kill the enclosing 'cmd' window.

If SuperCollider goes crazy the options are to turn off the sound or close the 'Command Prompt' window to restart
it.  It should be easy to turn the power to the speakers off in the case of craziness so that it doesn't require
somebody knowing how to get on the computer and kill the 'Command Prompt' window.

Overrall, it has been stable but if you start overlapping synth requests it can get a little funky because everything
is running via streaming from disk so you end up in a situation where two synths are reading from the same buffer.