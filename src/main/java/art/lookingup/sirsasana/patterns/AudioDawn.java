package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.*;
import heronarts.lx.LX;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

/**
 * Plays the dawn chorus.  One track will play out of all six speakers and all birds will respond to the same
 * track.
 */
public class AudioDawn extends LXPattern {
  // This should never be shorter than the track length, which is currently 2 minutes and 7 seconds.
  DiscreteParameter minSingingPause = new DiscreteParameter("sngPaus", 129, 127, 1200).setDescription("Minimum time between bird re-singing");
  CompoundParameter perBirdVolume = new CompoundParameter("bVol", 0.09, 0.00, 0.2).setDescription("Bird volume");
  DiscreteParameter randomStartDelay = new DiscreteParameter("bRndStart", 30, 0, 300).setDescription("Random start delay for birds");
  CompoundParameter ampScaleP = new CompoundParameter("ampScale", 1f, 0f, 10f).setDescription("Amplitude control signal scale");

  protected boolean waitingToPlay = true;
  protected boolean playing = false;
  protected long lastSinging = 0;
  protected long startPlayingAt = 0;

  public AudioDawn(LX lx) {
    super(lx);
    addParameter("sngPaus", minSingingPause);
    addParameter("bVol", perBirdVolume);
    addParameter("bRndStart", randomStartDelay);
    addParameter("ampScale", ampScaleP);
  }

  public void run(double deltaMs) {
    long now = System.currentTimeMillis();
    if (now - lastSinging > minSingingPause.getValuei() * 1000 && !waitingToPlay) {
      // Eligible to pick next start time.
      startPlayingAt = now + (int)(Math.random() * randomStartDelay.getValuei() * 1000f);
      waitingToPlay = true;
      playing = false;
    } else if (now > startPlayingAt && !playing) {
      float volume = BirdSongGen.remapStereoVolume(perBirdVolume.getValuef());
      Synth.playChorus("chorus", "dawn", volume, ampScaleP.getValuef());
      lastSinging = now;
      waitingToPlay = false;
      playing = true;
    }
  }
}
