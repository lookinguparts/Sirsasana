// Load bird buffers, load bird midi, register OSC message handlers
Server.default = s = Server.internal.boot;

s.waitForBoot {
	~wren = Buffer.read(s, "C:\\wren.wav");
	(
		SynthDef("bird", {
			arg channel = 0, volume = 0.3;
			var playBuf = PlayBuf.ar(1, ~wren, doneAction: Done.freeSelf);
			Out.ar(channel, playBuf * volume);
		}).add();
	)
	~birdSynths = ["bird", "bird", "bird", "bird", "bird", "bird"];
	// Load the bird MIDI file
	~birdmidifile = SimpleMIDIFile.read( "C:\\Users\\Tracy\\Documents\\bird.mid" );
	// Get the MIDI data a simple sequence
	~birdmidiseq = ~birdmidifile.generatePatternSeqs;
	// Initialize the MIDI client
	MIDIClient.init;
	// Select the loopMIDI midi output.  This is what LXStudio will be listening to.
	~loopmidiout =  MIDIOut.newByName("loopMIDI Port", "loopMIDI Port"); //MIDIOut(0, MIDIClient.destinations.at(1).uid);
	// Turn the raw note data into a MIDI event stream bound to our midi out.
	// TODO: We will need to either dynamically bind MIDI \chan or call this 12 times with different \chan bindings.
	~birdmidi = Pbind(\type, \midi, \chan, 3, \midiout, ~loopmidiout, [\midinote, \dur], Pseq(~birdmidiseq[1], 1));
	~birdmidis = [~birdmidi, ~birdmidi, ~birdmidi, ~birdmidi, ~birdmidi, ~birdmidi];
	~host = NetAddr("localhost", NetAddr.langPort);
	~responder = OSCresponder(
		~host, "/bird",
		{|time, responder, message, address|
			"triggering bird: ".post;
			message[1].postln;
			Synth(~birdSynths[message[1]], [\channel, message[1], \volume, message[2]]);
			~birdmidis[message[1]].play();
		}
	).add;
}

~host.sendMsg("/bird", 3, 0.8);

Synth("bird",[\channel, 4, \volume, 0.7]);

(
6.do({
		arg cnt1;
		~host.sendMsg("/bird", cnt1, 0.8);
	});
)