package art.lookingup.ui;

import art.lookingup.sirsasana.Output;
import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

public class MapOffsets extends UIConfig {
  public static final String CROWN = "crown";
  public static final String UPPER_RING = "upperring";
  public static final String LOWER_RING_UP = "lowerringup";
  public static final String LOWER_RING_DOWN = "lowerringdown";
  public static final String GROUND = "ground";
  public static final String CANOPY = "canopy";
  public static final String BIRD = "bird";
  public static final String title = "map offsets";
  public static final String filename = "mapoffsets.json";
  public LX lx;
  private boolean parameterChanged = false;

  public MapOffsets(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    this.lx = lx;

    registerStringParameter(CROWN, "0");
    registerStringParameter(UPPER_RING, "0");
    registerStringParameter(LOWER_RING_UP, "0");
    registerStringParameter(LOWER_RING_DOWN, "0");
    registerStringParameter(GROUND, "0");
    registerStringParameter(CANOPY, "0");
    registerStringParameter(BIRD, "0");

    save();

    buildUI(ui);
  }

  public int getCrownOffset() {
    return Integer.parseInt(getStringParameter(CROWN).getString());
  }

  public int getUpperRingOffset() {
    return Integer.parseInt(getStringParameter(UPPER_RING).getString());
  }

  public int getLowerRingUpOffset() {
    return Integer.parseInt(getStringParameter(LOWER_RING_UP).getString());
  }

  public int getLowerRingDownOffset() {
    return Integer.parseInt(getStringParameter(LOWER_RING_DOWN).getString());
  }

  public int getGroundOffset() {
    return Integer.parseInt(getStringParameter(GROUND).getString());
  }

  public int getCanopyOffset() {
    return Integer.parseInt(getStringParameter(CANOPY).getString());
  }

  public int getBirdOffset() {
    return Integer.parseInt(getStringParameter(BIRD).getString());
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    parameterChanged = true;
  }

  @Override
  public void onSave() {
    if (parameterChanged) {
      Output.restartOutput(lx);
      parameterChanged = false;
    }
  }
}
