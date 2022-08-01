package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

@LXCategory(LXCategory.FORM)
public class Selector extends LXPattern {

  static public String[] sections = {
      "Spike",
      "Upper",
      "Lower Up",
      "Lower Down",
      "Ground"
  };

  DiscreteParameter which = new DiscreteParameter("section", sections);
  ColorParameter color = new ColorParameter("color");

  public Selector(LX lx) {
    super(lx);
    addParameter(which);
    addParameter(color);
  }


  public void run(double deltaMs) {
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }
    switch (which.getValuei()) {
      case 0:
        for (LXPoint p : SirsasanaModel.topCrownSpikeLights)
          colors[p.index] = color.getColor();
        break;
      case 1:
        for (LXPoint p : SirsasanaModel.upperRingFloods)
          colors[p.index] = color.getColor();
        break;
      case 2:
        for (LXPoint p : SirsasanaModel.lowerRingUpFloods)
          colors[p.index] = color.getColor();
        break;
      case 3:
        for (LXPoint p : SirsasanaModel.lowerRingDownFloods)
          colors[p.index] = color.getColor();
        break;
      case 4:
        for (LXPoint p : SirsasanaModel.baseFloods)
          colors[p.index] = color.getColor();
        break;
    }
  }
}
