OUTPUT MAPPING

The global content pane has an 'output map' panel.  out1, out2, etc. correspond to the Pixlite Outputs.
Various sculpture lighting components can be assigned to specific Pixlite outputs.  Multiple components
can be assigned to a single Pixlite output by separating the components with a comma (no spaces).  For
example the default configuration for output3 is bringup,bringdown which means the bottom ring up pointing floods
followed by the bottom ring down pointing floods.

Shorthands:
gf = ground floods
b = bird
topring = floods on upper ring
crown = lights on the crown
cg = canopy group which means both lights at a particular canopy location.

!IMPORTANT!
You must pick some location as polar angle 0 and the wiring must start at that location consistently and then
travel the wiring counterclockwise (when looking down from the top) around the installation.  It is especially
important that for groups of lights like the ground floods or a pair of canopy lights that they are wired
in a counter-clockwise direction because in order to fix it you will need to make source code changes.

If you don't pick a consistent reference point across all layers of the installation then the different levels
of lights might be out of position with respect to rotation around the central axis. That will cause problems
for rotational sweeping effects between the various components.

In the case that something went wrong with wiring and the wiring for the various components don't start at
the proper polar angle (azimuth around the sculpture) there is a 'map offsets' panel. For example, if the top floods
are wired so that they don't line up with the bottom floods, you set the offset to shift the starting point for which
flood LX Studio thinks is the first flood in terms of data wiring.  For groups of lights like the ground floods
and canopy lights, the offset is based on the set of lights.  So for example, the only valid offset values for
the ground floods are 0, 1, 2 since there are 3 groups of lights.  For the canopy lights the only valid offsets are
0, 1, 2, 3, 4, 5 since there are 6 sets of canopy lights.

