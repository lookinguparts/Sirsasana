
Quarks.gui
m = SimpleMIDIFile.read( "C:\\Users\\Tracy\\Documents\\bird.mid" );
m.tempo;
SimpleMIDIFile.tempoScale;
m.getTempo;

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
~birdbus = Bus.control();  	// create a Bus to store amplitude data
{
	var sound = PlayBuf.ar(1, ~wren)* 0.8;
	var amp = Amplitude.kr(sound, 0.05, 0.1) * 30.0;
	Out.kr(~birdbus, amp);
	Out.ar(0, sound);
}.play;

{
	var sound = PlayBuf.ar(1, ~wren)* 0.8;
	var amp = Amplitude.kr(sound);
	Out.kr(~birdbus, amp);
	Out.ar(1, sound);
}.play;

~birdhost = NetAddr("localhost", 7979);
~birdbus.scope();

~vollistener = fork{loop{
	~birdbus.get({
		arg value;
		{
			var z = if (value < 0.0000001, {0}, {value});
			~birdhost.sendMsg("/sirsasana/birdvol", 0, z);
		}.defer(); // schedule in the AppClock
	});
	0.05.wait; // indicator will be updated every .1 seconds
}};

{Out.ar(3, PlayBuf.ar(1, ~wren)* 0.8)}.play;
{Out.ar(5, PlayBuf.ar(1, ~wren)* 0.8)}.play;
{Out.ar(0, PlayBuf.ar(1, ~wren, loop: 1)* 1.0)}.play;

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


~host = NetAddr("localhost", 57123);


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

~host.sendMsg("/speaker", 1, 0.8);

Synth("bird",[\channel, 4, \volume, 0.7]);

(
6.do({
		arg cnt1;
		~host.sendMsg("/bird", cnt1, 0.8);
	});
)

	~bird1Path = ~projectPath ++ "XC199847.wav";
	~bird1 = Buffer.read(s, ~bird1Path);
	~bird1;
	~bird2Path = ~projectPath ++ "XC199849.wav";
	~bird2 = Buffer.read(s, ~bird2Path);
	(
		SynthDef("bird1", {
			arg channel = 0, volume = 0.3;
			var playBuf = PlayBuf.ar(1, ~bird1, BufRateScale.kr(~bird1), doneAction: Done.freeSelf);
			Out.ar(channel, playBuf * volume);
		}).add();
	)
	(
		SynthDef("bird2", {
			arg channel = 0, volume = 0.3;
			var playBuf = PlayBuf.ar(1, ~bird2, doneAction: Done.freeSelf);
			Out.ar(channel, playBuf * volume);
		}).add();
	)
	Synth("bird1").play();




