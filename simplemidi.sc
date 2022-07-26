
Quarks.gui
m = SimpleMIDIFile.read( "C:\\Users\\Tracy\\Documents\\bird.mid" );
t = m.generatePatternSeqs;
MIDIClient.init;
c = MIDIOut(0, MIDIClient.destinations.at(1).uid);
~loopMidiOutPort = MIDIOut.newByName("loopMIDI Port", "loopMIDI Port");
~loopMidiOutPort.noteOn(2);
~loopMidiOutPort.noteOff(2);
c.noteOn(2);
c.noteOff(2);
t.postln;
q = Pbind([\midinote, \dur], Pseq(t[1], 1));
q.play();
q = Pbind(\type, \midi, \midiout, c, [\midinote, \dur], Pseq(t[1], 1));
q.play();
~wrenmidi = Pbind(\type, \midi, \chan, 3, \midiout, c, [\midinote, \dur], Pseq(t[1], 1));
~wrenmidiloop = Pbind(\type, \midi, \chan, 3, \midiout, c, [\midinote, \dur], Pseq(t[1], inf));
~wrenplaying = ~wrenmidiloop.play();
~wrenplaying.stop();
~wrenmidi.play();
~wrenmidis = [~wrenmidi];
~birdmidis = [~wrenmidi];
~wren = Buffer.read(s, "C:\\wren.wav");
(
{Out.ar(0, PlayBuf.ar(1, ~wren)* 0.8)}.play;
{Out.ar(3, PlayBuf.ar(1, ~wren)* 0.8)}.play;
{Out.ar(5, PlayBuf.ar(1, ~wren)* 0.8)}.play;
)

(
SynthDef("bird", {
	arg channel = 0, volume = 0.3;
	var playBuf = PlayBuf.ar(1, ~wren, doneAction: Done.freeSelf);
	Out.ar(channel, playBuf * volume);
}).add();
)
~wrenmidis[0].play();
Synth("bird", [\channel, 4, \volume, 0.1]);
~wrensynths = ["bird"];
~birdsynths = ["bird"];


~host = NetAddr("localhost", NetAddr.langPort);

~host.sendMsg("/bird2", 0, 0.8);

(
~responder = OSCresponder(
	~host, "/bird2",
	{|time, responder, message, address|
		[\responder, time, message, address].postln;
		message[1].postln;
		Synth(~birdsynths[message[1]], message[2]);
		~birdmidis[0].play();
	}
).add;
)

Platform.userConfigDir.postln; // +/+ "startup.scd"  is the initialization file.




