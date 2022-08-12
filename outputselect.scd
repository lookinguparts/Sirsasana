Server.default = s = Server.internal.boot;

{Out.ar(1, SinOsc.ar)}.play;

o = Server.default.options;
o.println;
o.inDevice_("ASIO : ASIO4ALL v2");
o.outDevice_("ASIO : ASIO4ALL v2");

Server.default.reboot;

{Out.ar(5, SinOsc.ar)}.play;

s.options.numOutputBusChannels = 8;
s.options.numOutputBusChannels.postln;


thisProcess.openPorts;