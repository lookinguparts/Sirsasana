GENERAL NOTES

VOLUMES:
The volume levels need to be adjusted for the actual installation.  Since I have never heard the actual set up the
current volume levels are not likely to be correct.

Each Audio* pattern has some volume adjustment knobs in it.  It isn't possible to adjust the volume of individual
birds but those audio files should be at their appropriate relative audio levels already.

For Tyson's audio, you can adjust the birds and the tree+subwoofer volumes independently.


VISUALS:
The current visuals are pretty basic since there is not a lot to do with flood lights and while I implemented a
previz in Unity the chances of it being accurate are pretty slim.

The overall intensity balance of flood lights can be controlled per sculptural section.  On the Master Out, you
can add an Effect to turn down the level for a specific section of the installation, for example GroundLevel.  If
one section is always too bright for example, you can use an effect to turn it down that will apply to all patterns.

PATTERNS:
VERTICAL:
This pattern positions a basic waveform (by default a triangle wave) at the specified position (pos).  The value is
normalized to the model min and model max.  For the triangle wave, you can adjust the sharpness of the triangle with
the slope parameter.  I've created a few presets for this pattern that effectively implement an upward moving glow.
To achieve that effect a linear ramp modulator is applied to the position.  A downward moving glow is achieved with a
downward moving linear ramp.  There is also an example of sine wave modulator that moves the glow back and forth.
There is also a square wave with a width parameter.  Since it is a square wave the lights will change discontinuously.
There is also a step decay wave which is like a square wave with a downward linear ramp at the back.  Like most
patterns, you can choose a constant color or a palette.  For the palette option, it will map the selected palette
instead of the wave controlling the brightness.