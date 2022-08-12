Server.internal.options.numInputBusChannels = 0;
// Load bird buffers, load bird midi, register OSC message handlers
Server.default = s = Server.internal.boot;
thisProcess.nowExecutingPath;
s.waitForBoot {
	~projectPath = PathName(thisProcess.nowExecutingPath).parentPath;
	~speakerPaths = [~projectPath ++ "XC199847.wav", ~projectPath ++ "XC199849.wav"];
	~speakerPathsAll = ~speakerPaths.dup(3);
	~speakerPathsAll = ~speakerPathsAll.flatten();
	~speakerPathsAll.collect({ arg bpath, i;
		var bird = Buffer.read(s, bpath);
		SynthDef("speaker" ++ (i+1), {
			arg channel = 0, volume = 0.8;
			var playBuf = PlayBuf.ar(1, bird, BufRateScale.kr(bird), doneAction: Done.freeSelf);
			Out.ar(channel, playBuf * volume);
		}).add();
	});
	~speakerSynths = ["speaker1", "speaker2", "speaker3", "speaker4", "speaker5", "speaker6"];
	// Load the bird MIDI file
	MIDIClient.init;
	// Select the loopMIDI midi output.  This is what LXStudio will be listening to.
	~loopmidiout =  MIDIOut.newByName("MidiPipe Input 1", "MidiPipe Input 1"); //MIDIOut(0, MIDIClient.destinations.at(1).uid);

	// Each Bird has a separate MIDI file, called bird
	~birdMidiFiles = ["bird1", "bird2", "bird3", "bird4", "bird5", "bird6", "bird7", "bird8", "bird9", "bird10", "bird11", "bird12"];

	~birdMidiGens = ~birdMidiFiles.collect({arg bmidi, i;
		var bmidifile = SimpleMIDIFile.read( ~projectPath ++ bmidi ++ ".mid" );
		var bmidiseq = bmidifile.generatePatternSeqs;
		var channel = i + 1;
		// Turn the raw note data into a MIDI event stream bound to our midi out.
		Pbind(\type, \midi, \chan, channel, \midiout, ~loopmidiout, [\midinote, \dur], Pseq(bmidiseq[1], 1));
	});

	o = OSCFunc({ arg message, time, addr, recvPort;
		var speakerIndex = message[1] - 1;
		[message, time, addr, recvPort].postln;
		"triggering speaker: ".post;
		message[1].postln;
		Synth(~speakerSynths[speakerIndex], [\channel,speakerIndex%2, \volume, message[2]]);
		~birdMidiGens[speakerIndex * 2].play();
		~birdMidiGens[speakerIndex * 2 + 1].play();
	}, '/speaker', nil, 5757);

	// Test data was exported from generic FL Studio session which as a default tempo of 130bpm so 130/60 to convert to beats per second
	TempoClock.tempo = 2.2;

	"OSC Port: ".post;
	NetAddr.langPort.postln;
	~host = NetAddr("localhost", NetAddr.langPort);
	~responder = OSCresponder(
		~host, "/speakerold",
		{|time, responder, message, address|
			var speakerIndex = message[1] - 1;
			"triggering speaker: ".post;
			message[1].postln;
			Synth(~speakerSynths[speakerIndex], [\channel,speakerIndex, \volume, message[2]]);
			~birdMidiGens[speakerIndex * 2].play();
			~birdMidiGens[speakerIndex * 2 + 1].play();
		}
	).add;
}

