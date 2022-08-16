package art.lookingup.sirsasana;

import heronarts.lx.LXLoopTask;

/**
 * Class that generates bird song.  For now it will just randomly trigger bird synths.
 */
public class BirdSongGen implements LXLoopTask {

  static public long minSingingPause = 30;  // in seconds;
  static public float perBirdVolume = 0.09f;

  public void loop(double deltaMs) {
    long now = System.currentTimeMillis();
    for (Bird bird : SirsasanaModel.birds) {
      if (now - bird.lastSinging > minSingingPause * 1000) {
        int channel = remapStereo(bird.id/2);
        float volume = remapStereoVolume(perBirdVolume);
        Synth.playBird("tracs", bird.id + 1, channel, volume, 2);
        bird.lastSinging = now;
      }
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
