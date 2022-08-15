Server.default = s = Server.internal.boot;

s.waitForBoot {
	// File for iteratively developing sirsasanagen.scd
	~projectPath = PathName(thisProcess.nowExecutingPath).parentPath;
	~files = PathName(~projectPath).entries;
	~dirs = [];
	~files.do {
		arg file;

		file.postln;
		if (file.isFolder, {~dirs.add(file)},{});
	}
	// ~dirs now has all directories.  For each directory, we will create a buffer for 1.wav through 12.wav.
	// We will need to create 12 control busses for each bird's volume tracking.
	// We will then create a SynthDef for each WAV file/aka bird with an Amplitude tracker on each SynthDef.
	// The Amplitude control rate UGen will write to the appropriate bird control bus.
	~dirs;

	// We will create 12 control busses, one for each bird.  Each time we create a SynthDef we will attach
	// an amplitude tracker that sends data to the corresponding control bus.  And then we need to create
	// 12 bus listeners that forward control bus data to LX Studio.
	~cbusses = 12.collect({arg i;
		Bus.control();
	});
	~cbusses.size;

	// For each directory, collect all entries which should be wav files.
	~dirs.do ({
		arg dirname;
		var wavs = dirname.entries;
		var synths = [];
		dirname.folderName.postln;
		wavs.postln;
		// For each wav file, create a synthdef with an attached volume tracker that sends
		// its data to a control bus.
		wavs.do({
			arg filename, i;
			var synthName = dirname.folderName ++ "_" ++ (i+1);
			var bird = Buffer.read(s, filename.fullPath);
			filename.postln;
			synthName.postln;
			SynthDef(synthName, {
				arg channel = 0, volume = 0.8, ampScale = 1.0;
				var playBuf = PlayBuf.ar(1, bird, BufRateScale.kr(bird), doneAction: Done.freeSelf);
				// Set the attack 0.05 and release 0.1 on the Amplitude so that the signal isn't too spikey.
				// Multiply by 30 to get to 0 to 1 range. LX Studio will clamp to 0 to 1.  Just get close.
				var amp = Amplitude.kr(playBuf * volume * ampScale, 0.05, 0.1) * 30.0;
				Out.kr(~cbusses[i], amp);
				Out.ar(channel, playBuf * volume);
			}).add();
		})
	});


	o = OSCFunc({ arg message, time, addr, recvPort;
		var channel = floor(message[2] / 2).asInteger;
		var synthName = message[1] ++ "_" ++ message[2];
		[message, time, addr, recvPort].postln;
		"triggering synth: ".post;
		synthName.postln;
		message[1].postln;
		message[2].postln;
		message[3].postln;
		message[4].postln;
		Synth(synthName, [\channel,channel, \volume, message[3], \ampScale, message[4]]);
	}, '/synth', nil, 5757);

	~lxstudiohost = NetAddr("localhost", 7979);

	// Create control bus listeners.  For each of the 12 control busses, create a listener that
	// sends an appropriate OSC message.
	12.do({
		arg i;
		var amplistener = fork{loop{
			~cbusses[i].get({
				arg value;
				{
					var z = if (value < 0.0000001, {0}, {value});
					~lxstudiohost.sendMsg("/sirsasana/birdvol", i, z);
				}.defer(); // shcedule in the AppClock
			});
			0.05.wait; // indicator will be updated every 0.05 seconds (20 times per second)
		}};
	});
}
