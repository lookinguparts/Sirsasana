package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.*;
import heronarts.lx.LX;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Pattern that just generates audio.
 *
 * Notes: Frog play time is < 8 seconds.
 */
public class AudioCustom extends LXPattern {
  public String[] customs = { "custom1", "custom2", "custom3", "custom4", "custom5", "custom6", "custom7"};

  DiscreteParameter whichSynth = new DiscreteParameter("custom", customs);

  CompoundParameter minLoop = new CompoundParameter("minLoop", 10, 1, 300).setDescription("Minimum loop time");
  CompoundParameter volume = new CompoundParameter("vol", 0.09, 0.00, 0.2).setDescription("Sample volume");
  DiscreteParameter randomStartDelay = new DiscreteParameter("bRndStart", 30, 0, 300).setDescription("Random start delay samples");
  CompoundParameter ampScaleP = new CompoundParameter("ampScale", 1f, 0f, 10f).setDescription("Amplitude feedback signal scale");

  static public class Sound {
    public Sound(int id) {
      this.id = id;
    }
    public int id;
    public long lastSinging = 0;
    public long startPlayingAt = 0;
    public boolean waitingToPlay = false;
    public boolean playing = false;
  }

  public List<Sound> sounds = new ArrayList<Sound>(12);

  public AudioCustom(LX lx) {
    super(lx);
    addParameter("custom", whichSynth);
    addParameter("minLoop", minLoop);
    addParameter("vol", volume);
    addParameter("bRndStart", randomStartDelay);
    addParameter("ampScale", ampScaleP);

    for (int i = 0; i < 12; i++) {
      sounds.add(new Sound(i));
    }
  }

  public void run(double deltaMs) {
    long now = System.currentTimeMillis();
    for (Sound sound : sounds) {
      if (now - sound.lastSinging > minLoop.getValue() * 1000 && !sound.waitingToPlay) {
        // Eligible to pick next start time.
        sound.startPlayingAt = now + (int)(Math.random() * randomStartDelay.getValuei() * 1000f);
        sound.waitingToPlay = true;
        sound.playing = false;
      } else if (now > sound.startPlayingAt && !sound.playing) {
        int channel = BirdSongGen.remapStereo(sound.id/2);
        float vol = BirdSongGen.remapStereoVolume(volume.getValuef());
        Synth.playBird(customs[whichSynth.getValuei()], sound.id + 1, channel, vol, ampScaleP.getValuef());
        sound.lastSinging = now;
        sound.waitingToPlay = false;
        sound.playing = true;
      }
    }
  }
}
