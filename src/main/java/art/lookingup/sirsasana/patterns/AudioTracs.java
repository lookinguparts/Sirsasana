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
 * Notes: Frog play time is < 8 seconds.
 */
public class AudioTracs extends LXPattern {
  DiscreteParameter minSingingPause = new DiscreteParameter("sngPaus", 30, 0, 300).setDescription("Minimum time between bird re-singing");
  DiscreteParameter minFrogPause = new DiscreteParameter("minFgPaus", 30, 0, 3600).setDescription("Minimum time between frog songs");
  CompoundParameter perBirdVolume = new CompoundParameter("bVol", 0.09, 0.00, 0.2).setDescription("Bird volume");
  DiscreteParameter randomStartDelay = new DiscreteParameter("bRndStart", 30, 0, 300).setDescription("Random start delay for birds");
  DiscreteParameter randomFrogStartDelay = new DiscreteParameter("fRndStart", 30, 0, 3600).setDescription("Random start delay for frog");
  CompoundParameter frogVolume = new CompoundParameter("fVol", 0.09, 0f, .2).setDescription("Frog volume");

  public AudioTracs(LX lx) {
    super(lx);
    addParameter("sngPaus", minSingingPause);
    addParameter("minFgPaus", minFrogPause);
    addParameter("bVol", perBirdVolume);
    addParameter("bRndStart", randomStartDelay);
    addParameter("fRndStart", randomFrogStartDelay);
    addParameter("fVol", frogVolume);
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
      if (now - bird.lastSinging > minSingingPause.getValuei() * 1000 && !bird.waitingToPlay) {
        // Eligible to pick next start time.
        bird.startPlayingAt = now + (int)(Math.random() * randomStartDelay.getValuei() * 1000f);
        bird.waitingToPlay = true;
        bird.playing = false;
      } else if (now > bird.startPlayingAt && !bird.playing) {
        int channel = BirdSongGen.remapStereo(bird.id/2);
        float volume = BirdSongGen.remapStereoVolume(perBirdVolume.getValuef());
        Synth.playBird("tracs", bird.id + 1, channel, volume, 2);
        bird.lastSinging = now;
        bird.waitingToPlay = false;
        bird.playing = true;
      }
    }

    // Maybe play the frog.
    if (now - Frog.lastSinging > minFrogPause.getValuei() * 1000 && !Frog.waitingToPlay) {
      Frog.startPlayingAt = now + (int)(Math.random() * randomFrogStartDelay.getValuei());
      Frog.waitingToPlay = true;
      Frog.playing = false;
    } else if (now > Frog.startPlayingAt && !Frog.playing) {
      int channel = ThreadLocalRandom.current().nextInt(0, 6);
      channel = BirdSongGen.remapStereo(channel);
      Synth.playSynth("tracs", "frog", channel, frogVolume.getValuef());
      Frog.lastSinging = now;
      Frog.waitingToPlay = false;
      Frog.playing = true;
    }
  }
}
