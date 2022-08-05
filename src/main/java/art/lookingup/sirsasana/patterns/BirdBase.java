package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.Bird;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.midi.*;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

import java.util.logging.Logger;

abstract public class BirdBase extends LXPattern implements LXMidiListener {
  private static final Logger logger = Logger.getLogger(BirdBase.class.getName());

  public DiscreteParameter midiChannel = new DiscreteParameter("midich", 1, 1, 17);
  public DiscreteParameter midiNote = new DiscreteParameter("note", 49, 0, 127);
  public BooleanParameter allSinging = new BooleanParameter("sing", false);
  public BooleanParameter allIdle = new BooleanParameter("idle", false);
  public BooleanParameter logNotes = new BooleanParameter("log", false);
  public CompoundParameter transition = new CompoundParameter("trans", 0f, 0f, 2000f)
      .setDescription("Transition time between singing and idle states");

  // We need to perform our own internal blending when transitioning from singing to idle and back
  public int[] blendBuffer;

  public BirdBase(LX lx) {
    super(lx);
    addParameter("midich", midiChannel);
    addParameter("note", midiNote);
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
        // TODO(tracy): If we are in a transition state, we will need to render both patterns and blend between them.
        if (bird.state == Bird.State.SINGING) {
          renderBirdSinging(colors, bird, deltaMs);
        } else if (bird.state == Bird.State.IDLE) {
          renderBirdIdle(colors, bird, deltaMs);
        } else {
          renderBirdSinging(colors, bird, deltaMs);
          renderBirdIdle(blendBuffer, bird, deltaMs);
          // Ugh, we need to just go through per-bird points only and blend them.
          for (LXPoint p : bird.points) {
            colors[p.index] = LXColor.lerp(colors[p.index], blendBuffer[p.index], bird.getIdleWeight(transition.getValuef()));
          }
        }
        bird.updateState(deltaMs, transition.getValuef());
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
   * TODO(tracy): A decent default bird idle animation should go here.
   * @param colors
   * @param bird
   * @param deltaMs
   */
  public void renderBirdIdle(int[] colors, Bird bird, double deltaMs) {
    for (LXPoint p : bird.points) {
      colors[p.index] = LXColor.gray(20);
    }
  }


  public void noteOffReceived(MidiNote note) {
    Bird bird = SirsasanaModel.midiToBird.get(note.getChannel());
    if (logNotes.isOn())
      logger.info("Note OFF for channel: " + note.getChannel() + " note val: " + note.getPitch());
    if (bird != null) {
      bird.stopSinging();
    }
  }

  // When we receive a note on, look up the bird and add it to singing birds.
  public void noteOnReceived(MidiNoteOn note) {
    Bird bird = SirsasanaModel.midiToBird.get(note.getChannel());
    if (logNotes.isOn())
      logger.info("Note ON for channel: " + note.getChannel() + " note val: " + note.getPitch());
    if (bird != null) {
      //logger.info("Changing bird to singing");
      bird.startSinging(note.getPitch());
    }
  }

  public void aftertouchReceived(MidiAftertouch aftertouch) {
  }

  public void controlChangeReceived(MidiControlChange cc) {
  }

  public void pitchBendReceived(MidiPitchBend pitchBend) {
  }

  public void programChangeReceived(MidiProgramChange pc) {
  }
}
