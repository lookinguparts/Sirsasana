package art.lookingup.sirsasana;

import heronarts.lx.LXLoopTask;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that generates bird song.  For now it will just randomly trigger bird synths.
 */
public class BirdSongGen implements LXLoopTask {

  public void loop(double deltaMs) {
    // These are now implemented as individual LXPatterns.
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
