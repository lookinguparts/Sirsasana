package art.lookingup.sirsasana;

import heronarts.lx.osc.OscFloat;
import heronarts.lx.osc.OscInt;
import heronarts.lx.osc.OscMessage;
import heronarts.lx.osc.OscString;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Wrapper class for interacting with SuperCollider.
 */
public class Synth {
  private static final Logger logger = Logger.getLogger(Synth.class.getName());

  /**
   * Plays a bird on the requested channel.
   * python synth.py tracs 3 1 0.02 10.0
   * @param pkgName
   * @param birdNum
   * @param channel
   * @param volume
   * @param ampScale
   */
  static public void playBird(String pkgName, int birdNum, int channel, float volume, float ampScale) {
    OscMessage synthTrigger = new OscMessage("/synth");
    OscString pkg = new OscString(pkgName);
    OscInt bird = new OscInt(birdNum);
    OscInt chan = new OscInt(channel);
    OscFloat vol = new OscFloat(volume);
    OscFloat ampS = new OscFloat(ampScale);
    synthTrigger.add(pkg);
    synthTrigger.add(bird);
    synthTrigger.add(chan);
    synthTrigger.add(vol);
    synthTrigger.add(ampS);

    try {
      SirsasanaApp.superColliderOsc.send(synthTrigger);
    } catch (IOException ioex) {
      logger.info("Error sending synth trigger: " + ioex.getMessage());
    }
  }

  static public void playSynth(String pkgName, String synthName, int channel, float volume, float ampScale) {

  }
}
