package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.*;
import heronarts.lx.LX;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Pattern that just generates audio.
 *
 * bird1 5:20.250 or 5:21
 * bird2 5:20.250 or 5:21
 * bird3 5:20.250 or 5:21
 * bird4 5:20.250 or 5:21
 * bird5 5:20.250 or 5:21
 * bird6 5:20.250 or 5:21
 * bird7 5:20.250 or 5:21
 * bird8 5:20.250 or 5:21
 * bird9 5:20.250 or 5:21
 * bird10 5:20.250 or 5:21
 * bird11 5:20.250 or 5:21
 * bird12 5:20.250 or 5:21
 * woofer 6:01
 * tree 6:01
 */
public class AudioTyson extends LXPattern {
  DiscreteParameter birdLoop = new DiscreteParameter("bloop", 362, 362, 3000).setDescription("Loop time for birds");
  DiscreteParameter treeLoop = new DiscreteParameter("treelp", 362, 362, 3000).setDescription("Loop time for tree/woofer");
  CompoundParameter perBirdVolume = new CompoundParameter("bVol", 0.09, 0.00, 0.2).setDescription("Bird volume");
  CompoundParameter ampScaleP = new CompoundParameter("ampScale", 1f, 0f, 10f).setDescription("Amplitude control signal scale");
  CompoundParameter treeVolume = new CompoundParameter("treeVol", 0.09, 0.00, 3.0).setDescription("Tree volume");
  CompoundParameter wooferVolume = new CompoundParameter("wfrVol", 0.09, 0.0, 1.0).setDescription("Woofer volume");
  DiscreteParameter treeChannel = new DiscreteParameter("treeCh", 6, 6, 8);
  DiscreteParameter wooferChannel = new DiscreteParameter("wfrCh", 7, 6, 8);

  // For the tree + woofer
  static public long lastSinging = 0;
  static public long startPlayingAt = 0;
  static public boolean waitingToPlay = false;
  static public boolean playing = false;

  public AudioTyson(LX lx) {
    super(lx);
    addParameter("loop", birdLoop);
    addParameter("bVol", perBirdVolume);
    addParameter("treeVol", treeVolume);
    addParameter("treelp", treeLoop);
    addParameter("wfrVol", wooferVolume);
    addParameter("ampScale", ampScaleP);
    addParameter("treeCh", treeChannel);
    addParameter("wfrCh", wooferChannel);
  }

  public void onActive() {
    super.onActive();
    // reset bird singing times.
    for (Bird b: SirsasanaModel.birds) {
      b.lastSinging = 0;
      b.waitingToPlay = false;
    }
  }

  public void run(double deltaMs) {
    long now = System.currentTimeMillis();
    for (Bird bird : SirsasanaModel.birds) {
      float timeSinceSinging = now - bird.lastSinging;
      int bLoop = birdLoop.getValuei();
      if (now - bird.lastSinging > birdLoop.getValuei() * 1000 && !bird.waitingToPlay) {
        // Eligible to pick next start time.
        bird.startPlayingAt = now;
        bird.waitingToPlay = true;
        bird.playing = false;
      } else if (now > bird.startPlayingAt && !bird.playing) {
        int channel = BirdSongGen.remapStereo(bird.id/2);
        float volume = BirdSongGen.remapStereoVolume(perBirdVolume.getValuef());
        Synth.playBird("tyson", bird.id + 1, channel, volume, ampScaleP.getValuef());
        bird.lastSinging = now;
        bird.waitingToPlay = false;
        bird.playing = true;
      }
    }

    // Trigger the tree + woofer loop
    if (now - lastSinging > treeLoop.getValuei() * 1000 && !waitingToPlay) {
      startPlayingAt = now;
      waitingToPlay = true;
      playing = false;
    } else if (now > startPlayingAt && !playing) {
      int channelTree = treeChannel.getValuei();
      int channelWoofer = wooferChannel.getValuei();
      channelTree = BirdSongGen.remapStereo(channelTree);
      channelWoofer = BirdSongGen.remapStereo(channelWoofer);
      Synth.playSynth("tyson", "tree", channelTree, treeVolume.getValuef());
      Synth.playSynth("tyson", "woofer", channelWoofer, wooferVolume.getValuef());
      lastSinging = now;
      waitingToPlay = false;
      playing = true;
    }
  }
}
