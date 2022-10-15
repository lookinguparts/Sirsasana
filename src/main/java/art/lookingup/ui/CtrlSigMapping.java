package art.lookingup.ui;

import art.lookingup.sirsasana.Output;
import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtrlSigMapping extends UIConfig {
  public static final String CTRLBASE = "ctrl";

  public static final String title = "ctrl sig map";
  public static final String filename = "ctrlsigmap.json";
  public LX lx;
  private boolean parameterChanged = false;

  String[] defaultMapping = {
      "1,2",
      "3,4",
      "5,6",
      "7,8",
      "9,10",
      "11,12",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
  };

  public CtrlSigMapping(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    this.lx = lx;

    for (int i = 0; i < 12; i++) {
      registerStringParameter(CTRLBASE + i, defaultMapping[i]);
    }

    save();

    rebuildMap();
    buildUI(ui);
  }

  static public Map<Integer, List<Integer>> ctrlMap = new HashMap<Integer, List<Integer>>();


  /**
   * Create a map from control signal numbers (0-based) to bird numbers (one based here).
   */
  public void rebuildMap() {
    ctrlMap.clear();
    for (int i = 0; i < 12; i++) {
      List<Integer> toBirds = new ArrayList<Integer>();
      String[] birds = getCtrlMapping(i).split(",");
      for (int j = 0; j < birds.length; j++) {
        toBirds.add(Integer.parseInt(birds[j]));
      }
      ctrlMap.put(i, toBirds);
    }
  }

  public String getCtrlMapping(int outputNum) {
    return getStringParameter(CTRLBASE + outputNum).getString();
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    parameterChanged = true;
  }

  @Override
  public void onSave() {
    if (parameterChanged) {
      // Rebuild control signal mapping.
      parameterChanged = false;
      rebuildMap();
    }
  }
}
