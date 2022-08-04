package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.midi.*;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

import java.util.HashMap;
import java.util.Map;

abstract public class BirdBase extends LXPattern implements LXMidiListener {

  public DiscreteParameter midiChannel = new DiscreteParameter("midich", 1, 1, 17);
  public DiscreteParameter midiNote = new DiscreteParameter("note", 49, 0, 127);
  public BooleanParameter allSinging = new BooleanParameter("sing", false);
  public BooleanParameter allIdle = new BooleanParameter("idle", false);

  public Map<Integer, SirsasanaModel.Bird> singingBirds = new HashMap<Integer, SirsasanaModel.Bird>();
  public Map<Integer, SirsasanaModel.Bird> notSingingBirds = new HashMap<Integer, SirsasanaModel.Bird>();

  public BirdBase(LX lx) {
    super(lx);
    addParameter("midich", midiChannel);
    addParameter("note", midiNote);
    addParameter("sing", allSinging);
    addParameter("idle", allIdle);
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
      for (SirsasanaModel.Bird bird : SirsasanaModel.birds) {
        if (singingBirds.containsKey(bird.id))
          renderBirdSinging(colors, bird, deltaMs);
        else
          renderBirdIdle(colors, bird, deltaMs);
      }
    }
    afterRender(deltaMs);
  }

  /**
   * Utility method for testing bird visuals.  Include a Boolean
   * @param deltaMs
   */
  public void runAllBirdsSinging(double deltaMs) {
    for (SirsasanaModel.Bird bird: SirsasanaModel.birds) {
      renderBirdSinging(colors, bird, deltaMs);
    }
  }

  public void runAllBirdsIdle(double deltaMs) {
    for (SirsasanaModel.Bird bird : SirsasanaModel.birds) {
      renderBirdIdle(colors, bird, deltaMs);
    }
  }

  public void beforeRender(double deltaMs) {}
  public void afterRender(double deltaMs) {}

  abstract public void renderBirdSinging(int[] colors, SirsasanaModel.Bird bird, double deltaMs);

  /**
   * TODO(tracy): A decent default bird idle animation should go here.
   * @param colors
   * @param bird
   * @param deltaMs
   */
  public void renderBirdIdle(int[] colors, SirsasanaModel.Bird bird, double deltaMs) {
    for (LXPoint p : bird.points) {
      colors[p.index] = LXColor.gray(20);
    }
  }


  public void noteOffReceived(MidiNote note) {
    SirsasanaModel.Bird bird = SirsasanaModel.midiToBird.get(note.getChannel());
    if (bird != null) {
      if (singingBirds.containsKey(bird.id))
        singingBirds.remove(bird.id);
      notSingingBirds.put(bird.id, bird);
    }
  }

  // When we receive a note on, look up the bird and add it to singing birds.
  public void noteOnReceived(MidiNoteOn note) {
    SirsasanaModel.Bird bird = SirsasanaModel.midiToBird.get(note.getChannel());
    if (bird != null) {
      if (notSingingBirds.containsKey(bird.id))
        notSingingBirds.remove(bird.id);
      singingBirds.put(bird.id, bird);
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
