package art.lookingup.sirsasana;

import heronarts.lx.LXLoopTask;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that generates bird song.  For now it will just randomly trigger bird synths.
 */
public class BirdSongGen implements LXLoopTask {

  static public long minSingingPause = 30;  // in seconds;
  static public long minFrogPause = 30; // in seconds
  static public float perBirdVolume = 0.09f;
  static public float randomStartDelay = 30f;
  static public float randomFrogStartDelay = 30f;
  static public float frogVolume = 0.09f;

  public void loop(double deltaMs) {
    long now = System.currentTimeMillis();
    for (Bird bird : SirsasanaModel.birds) {
      if (now - bird.lastSinging > minSingingPause * 1000 && !bird.waitingToPlay) {
        // Eligible to pick next start time.
        bird.startPlayingAt = now + (int)(Math.random() * randomStartDelay * 1000f);
        bird.waitingToPlay = true;
        bird.playing = false;
      } else if (now > bird.startPlayingAt && !bird.playing) {
        int channel = remapStereo(bird.id/2);
        float volume = remapStereoVolume(perBirdVolume);
        Synth.playBird("tracs", bird.id + 1, channel, volume, 2);
        bird.lastSinging = now;
        bird.waitingToPlay = false;
        bird.playing = true;
      }
    }

    // Maybe play the frog.
    if (now - Frog.lastSinging > minFrogPause * 1000 && !Frog.waitingToPlay) {
      Frog.startPlayingAt = now + (int)(Math.random() * randomFrogStartDelay);
      Frog.waitingToPlay = true;
      Frog.playing = false;
    } else if (now > Frog.startPlayingAt && !Frog.playing) {
      int channel = ThreadLocalRandom.current().nextInt(0, 6);
      channel = remapStereo(channel);
      Synth.playSynth("tracs", "frog", channel, frogVolume, 2);
      Frog.lastSinging = now;
      Frog.waitingToPlay = false;
      Frog.playing = true;
    }
  }

  static public float remapStereoVolume(float volume) {
    if (SirsasanaApp.stereo) {
      return volume/3f;
    } else {
      return volume;
    }
  }

  static public int remapStereo(int channel) {
    if (SirsasanaApp.stereo) {
      return channel % 2;
    } else {
      return channel;
    }
  }
}
