Server.default = s = Server.internal.boot;

~oscUpdateSecs = 0.06;

s.waitForBoot {
	// File for iteratively developing sirsasanagen.scd
	~projectPath = PathName(thisProcess.nowExecutingPath).parentPath;
	// For non-interactive mode, set it to current directory where the script is run from.
	~projectPath = ".";
	~files = PathName(~projectPath).entries;
	~dirs = [];
	~files.do( {
		arg file;
		if (file.isFolder, {~dirs = ~dirs.add(file)},{});
	});

	// ~dirs now has all directories.  For each directory, we will create a buffer for 1.wav through 12.wav.
	// We will need to create 12 control busses for each bird's volume tracking.
	// We will then create a SynthDef for each WAV file/aka bird with an Amplitude tracker on each SynthDef.
	// The Amplitude control rate UGen will write to the appropriate bird control bus.

	// We will create 12 control busses, one for each bird.  Each time we create a SynthDef we will attach
	// an amplitude tracker that sends data to the corresponding control bus.  And then we need to create
	// 12 bus listeners that forward control bus data to LX Studio.
	~cbusses = 12.collect({arg i;
		Bus.control();
	});
	~cbusses.size;

	// We will track our diskin buffers via a dictionary.
	~allBuffers = Dictionary();
	//~allBufferPaths = Dictionary();

	// Track the buffer objects created to play the dawn chorus.  It will be 1 file with 1 buffer
	// multichannel expanded to 6 outs and 12 control busses.
	~chorusBuffers = Dictionary();

	"Found directories: ".post;
	~dirs.postln;
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
			var birdBuf; //= Buffer.read(s, filename.fullPath);
			var birdNum;
			var synthName;
			var filebase = PathName(filename.fileName).fileNameWithoutExtension;
			var bufReady;
			var synthBuf;
			//var filenameAbsolute = "C:\\Users\\Tracy\\Documents\\GitHub\\sirsasana\\sc\\" +/+ filename.fullPath.replace(".\\", "");
			if (filebase.contains("bird"), {
				birdNum = filebase.findRegexp("\\d+")[0][1].asInteger();
				synthName = dirname.folderName ++ "_" ++ birdNum;
				"creating bird synth:".post;
				synthName.postln;
				// This will allocate the buffer on the server.
				synthBuf = Buffer.cueSoundFile(s, filename.fullPath, 0, 1, bufferSize: 65536);
				// We need to re-cue the buffer with our synthBuf instance to get it to bind the filename in synthBuf
				// Not sure why this is required but it is.
				bufReady = { arg buf; "buffer ready".postln;};
				synthBuf.cueSoundFile(filename.fullPath, 0, 1, completionMessage: bufReady);
				// Store the buffer so we can pass it into the synthdef when playing. Each time we play, we need to
				// cue the buffer and then start the synth in the completionMessage: when the buffer is ready.
				~allBuffers.put(synthName, synthBuf);
				//~allBufferPaths.put(synthName, filename.fullPath);
				//~allBufferPaths.put(synthName, filenameAbsolute);
				SynthDef(synthName, {
					arg channel = 0, volume = 0.8, ampScale = 1.0, bufnum = 0;
					var diskin = DiskIn.ar(1, bufnum, 0);
					// var playBuf = PlayBuf.ar(1, bird, BufRateScale.kr(bird), doneAction: Done.freeSelf);
					// Set the attack 0.05 and release 0.1 on the Amplitude so that the signal isn't too spikey.
					// Multiply by 30 to get to 0 to 1 range. LX Studio will clamp to 0 to 1.  Just get close.
					var amp = Amplitude.kr(diskin * volume * ampScale, 0.05, 0.1) * 30.0;

					FreeSelfWhenDone.kr(diskin);
					Out.kr(~cbusses[birdNum-1], amp);
					Out.ar(channel, diskin * volume);
				}).add();
			}, {
				if (dirname.folderName.contains("chorus"), {
					// For the dawn chorus, we have 1 file that will be replicated to 6 outputs.
					synthName = dirname.folderName ++ "_" ++ filebase;
					"creating chorus synth:".post;
				    synthName.postln;
					// This will allocate the buffer on the server.
				    synthBuf = Buffer.cueSoundFile(s, filename.fullPath, 0, 1, bufferSize: 131072);
					// We need to re-cue the buffer with our synthBuf instance to get it to bind the filename in synthBuf
				    // Not sure why this is required but it is.
				    bufReady = { arg buf; "buffer ready".postln;};
				    synthBuf.cueSoundFile(filename.fullPath, 0, 1, completionMessage: bufReady);
					~allBuffers.put(synthName, synthBuf);
					// We can use multichannel expansion on the Out.kr and Out.ar
					SynthDef(synthName, {
						arg volume = 0.3, ampScale = 1.0, bufnum = 0;
						var diskin = DiskIn.ar(1, bufnum, 0);
						var amp = Amplitude.kr(diskin * volume * ampScale, 0.05, 0.1) * 30.0;
						FreeSelfWhenDone.kr(diskin);
						Out.kr(~cbusses, amp);
						Out.ar([0,1,2,3,4,5], diskin * volume);
					}).add();
			    },
				{
				synthName = dirname.folderName ++ "_" ++ filebase;
				"creating generic synth: ".post;
				synthName.postln;
				// NOTE(tracy): This is copied from above. It would be good to refactor this a bit.  Birds have both
				// specific synth names like tracs_1, etc. and also get Amplitude followers routed to control busses.
				// This will allocate the buffer on the server.
				synthBuf = Buffer.cueSoundFile(s, filename.fullPath, 0, 1, bufferSize: 131072);
				// We need to re-cue the buffer with our synthBuf instance to get it to bind the filename in synthBuf
				// Not sure why this is required but it is.
				bufReady = { arg buf; "buffer ready".postln;};
				synthBuf.cueSoundFile(filename.fullPath, 0, 1, completionMessage: bufReady);
				// Store the buffer so we can pass it into the synthdef when playing. Each time we play, we need to
				// cue the buffer and then start the synth in the completionMessage: when the buffer is ready.
				~allBuffers.put(synthName, synthBuf);
				//~allBufferPaths.put(synthName, filenameAbsolute);
				SynthDef(synthName, {
					arg channel = 0, volume = 0.8, ampScale = 1.0, bufnum = 0;
					//var playBuf = PlayBuf.ar(1, bird, BufRateScale.kr(bird), doneAction: Done.freeSelf);
					var diskin = DiskIn.ar(1, bufnum, 0);
	                FreeSelfWhenDone.kr(diskin);
					Out.ar(channel, diskin * volume);
				}).add();
			});
			});
		})
	});

	"All Buffers: ".postln;
	~allBuffers.postln;

	o = OSCFunc({ arg message, time, addr, recvPort;
		var channel = message[3];
		var synthName = message[1] ++ "_" ++ message[2];
		var synthBuf;
		var bufReady;
		var fname;
		[message, time, addr, recvPort].postln;
		"triggering synth: ".post;
		synthName.postln;
		synthBuf = ~allBuffers.at(synthName);
		//"synthBuf path: ".post;
		//synthBuf.path.postln;
		fname = synthBuf.path;
		//~allBufferPaths.at(synthName).postln;
		//File.getcwd.postln;
		synthBuf.close();
		bufReady = { arg buf;
			//"buffer ready".postln;
			//buf.postln;
			Synth(synthName, [\channel,channel, \volume, message[4], \ampScale, message[5], \bufnum, buf.bufnum]);
			1;
		};
		synthBuf.cueSoundFile(fname, 0, 1, completionMessage: bufReady);

	}, '/synth', nil, 5757);

    OSCFunc({ arg message, time, addr, recvPort;
		var synthName = message[1] ++ "_" ++ message[2];
		var synthBuf;
		var bufReady;
		var fname;
		[message, time, addr, recvPort].postln;
		"triggering chorus synth: ".post;
		synthName.postln;
		synthBuf = ~allBuffers.at(synthName);
		fname = synthBuf.path;
		//"synthBuf path: ".post;
		//synthBuf.path.postln;
		//~allBufferPaths.at(synthName).postln;
		//File.getcwd.postln;
		synthBuf.close();
		bufReady = { arg buf;
			//"buffer ready".postln;
			//buf.postln;
			Synth(synthName, [\volume, message[3], \ampScale, message[4], \bufnum, buf.bufnum]);
			1;
		};
		synthBuf.cueSoundFile(fname, 0, 1, completionMessage: bufReady);

	}, '/chorus', nil, 5757);

	OSCFunc({ arg message, time, addr, recvPort;
		thisProcess.shutdown;
		0.exit;
	}, '/exit', nil, 5757);

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
					if (z > 0.01, {
						~lxstudiohost.sendMsg("/sirsasana/birdvol", i, z);
					},{});
				}.defer(); // shcedule in the AppClock
			});
			~oscUpdateSecs.wait; // indicator will be updated every 0.05 seconds (20 times per second)
		}};
	});
}
