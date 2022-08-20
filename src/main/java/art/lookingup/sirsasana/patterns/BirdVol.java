package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.Bird;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.pattern.LXPattern;

import java.util.logging.Logger;

/**
 * Base class for mixing bird singing/idle looks by volume level received from SuperCollider.
 * volume = 1 is full singing and volume = 0 is full idle.
 */
abstract public class BirdVol extends LXPattern {
  private static final Logger logger = Logger.getLogger(BirdBase.class.getName());

  public BooleanParameter mix = new BooleanParameter("mix", false);
  public BooleanParameter allSinging = new BooleanParameter("sing", false);
  public BooleanParameter allIdle = new BooleanParameter("idle", false);
  public BooleanParameter logNotes = new BooleanParameter("log", false);
  public CompoundParameter transition = new CompoundParameter("trans", 0f, 0f, 2000f)
      .setDescription("Transition time between singing and idle states");

  // We need to perform our own internal blending when transitioning from singing to idle and back
  public int[] blendBuffer;

  public BirdVol(LX lx) {
    super(lx);
    addParameter("mix", mix);
    addParameter("sing", allSinging);
    addParameter("idle", allIdle);
    addParameter("log", logNotes);
    addParameter("trans", transition);
    blendBuffer = new int[colors.length];
  }

  /**
   * By default, whether a bird is singing or idle is tracked by midi notes on separate midi channels.  For testing
   * purposes there are manual override buttons that will put all birds in singing mode or all birds in idle mode.
   */
  public void run(double deltaMs) {
    beforeRender(deltaMs);
    if (allSinging.isOn()) {
      runAllBirdsSinging(deltaMs);
    } else if (allIdle.isOn()) {
      runAllBirdsIdle(deltaMs);
    } else {
      for (Bird bird : SirsasanaModel.birds) {
        renderBirdSinging(colors, bird, deltaMs);
        if (mix.isOn()) {
          renderBirdIdle(blendBuffer, bird, deltaMs);
          // Ugh, we need to just go through per-bird points only and blend them.
          for (LXPoint p : bird.points) {
            colors[p.index] = LXColor.lerp(colors[p.index], blendBuffer[p.index], 1f - bird.getVolume());
          }
        }
      }
    }
    afterRender(deltaMs);
  }

  /**
   * Utility method for testing bird visuals.  Include a Boolean
   * @param deltaMs
   */
  public void runAllBirdsSinging(double deltaMs) {
    for (Bird bird: SirsasanaModel.birds) {
      renderBirdSinging(colors, bird, deltaMs);
    }
  }

  public void runAllBirdsIdle(double deltaMs) {
    for (Bird bird : SirsasanaModel.birds) {
      renderBirdIdle(colors, bird, deltaMs);
    }
  }

  public void beforeRender(double deltaMs) {}
  public void afterRender(double deltaMs) {}

  abstract public void renderBirdSinging(int[] colors, Bird bird, double deltaMs);

  /**
   * @param colors
   * @param bird
   * @param deltaMs
   */
  public void renderBirdIdle(int[] colors, Bird bird, double deltaMs) {
    for (LXPoint p : bird.points) {
      colors[p.index] = LXColor.gray(20);
    }
  }
}
