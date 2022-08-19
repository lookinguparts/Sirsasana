GLITCHES

There are a number of things to try if you are experiencing audio glitches.  To understand the source of glitching
it helps to understand SuperCollider's synthesis internals.  Effectively, there is a graph of nodes describing
the state of the system.  The OS's audio driver constantly requests samples to keep the audio buffer full and
streaming out.  SuperCollider needs to evaluate it's graph each time the audio driver needs new samples.  This
should happen every few milliseconds.  I've configured the SuperCollider blockSize to 256 samples so at 48k
sample rate that is about every 5ms (note, we are running at 44.1kz since all the audio files are at that rate).
The default blockSize is 64 samples.  Increasing it has certain implications (control rate processes run per-block
so increasing the block size slows them down but the only control rate process we have is sending OSC amplitude
messages to LX Studio and even those are currently slowed down to 20 times per second).

The graph evaluation is the real-time core and if SuperCollider can't come up with the samples in time then the audio
will drop out.  By increasing the block size, we give SuperCollider more time to come up with the samples.

All of the sound samples are streaming from disk.  This is good because some of them are 20+ MB.  It is also bad
because the disk IO has to keep the audio buffer fed.  The default buffer size is 32K but I've increased it to
128K because nothing is interactive and at 32K there was some audio drop out issues.

Another source of glitching is that if LX Studio re-triggers one of the WAV files before it has finished playing.
Since we are streaming the audio from disk, the synth is configured to read from our disk-streaming buffer.  When
LX Studio triggers a WAV file the server will instantiate a new synth node in the processing graph and pass it
the disk-streaming buffer.  If there is already another synth reading from the buffer the conflicting uses of the
buffer will cause audio glitching.  This should only happen if the audio is still playing and LX Studio is
restarted and immediately attempts to play the same audio.  It should eventually work itself out.  If it doesn't
then a simple solution is to kill the Command Prompt window with SuperCollider in it.  AutoHotkey should restart it
and then eventually LX Studio will trigger the synths again (As long as the Audio* pattern is enabled, LX Studio
will continually retrigger the associated synths).  If you want to safely restart LX Studio, you can disable
the audio channel, wait for the currently playing synths to finish and then exit).

Since the different Audio* patterns use different disk-streaming buffers, it is okay to switch between an actively
playing Audio* pattern and another Audio* pattern.  You will just hear both patterns being output at the same
time until the synths from the previous Audio* pattern play themselves out.


More detailed info:
https://scsynth.org/t/real-time-audio-processing-or-why-its-actually-easier-to-have-a-client-server-separation-in-supercollider/2073