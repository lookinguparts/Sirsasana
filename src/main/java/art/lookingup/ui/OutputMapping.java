package art.lookingup.ui;

import art.lookingup.sirsasana.Output;
import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

public class OutputMapping extends UIConfig {
  public static final String OUTPUTBASE = "out";

  public static final String title = "output map";
  public static final String filename = "outputmap.json";
  public LX lx;
  private boolean parameterChanged = false;

  String[] defaultOutputMapping = {
      "crown",
      "topring",
      "bringdown,bringup",
      "gf.1",
      "gf.2",
      "gf.3",
      "cg.1,cg.2,cg.3,cg.4,cg.5,cg.6",
      "b.1,b.2",
      "b.3,b.4",
      "b.5,b.6",
      "b.7,b.8",
      "b.9,b.10",
      "b.11,b.12",
      "",
      "",
      "",

      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
  };

  public OutputMapping(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    this.lx = lx;

    for (int i = 1; i <= 32; i++) {
      registerStringParameter(OUTPUTBASE + i, defaultOutputMapping[i-1]);
    }

    save();

    buildUI(ui);
  }

  public String getOutputMapping(int outputNum) {
    return getStringParameter(OUTPUTBASE + outputNum).getString();
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
