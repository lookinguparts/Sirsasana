package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

public class GlowUp extends AnimT {
  ColorParameter color = new ColorParameter("color");

  public GlowUp(LX lx) {
    super(lx);
    addParameter(color);
    registerPhase("base",1f, 3f, "Base");
    registerPhase("lower", 1f, 3f, "Lower");
    registerPhase("upper", 1f, 3f, "Upper");
    registerPhase("spike", 1f, 3f, "Spike");
    color.brightness.setValue(100.0);
  }

  public void renderPhase(int curAnimPhase, float phaseLocalT) {
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }
    // Max brightness of 1 at t = 0.5 and then min at 0 and 1.
    float brightness = EaseUtil.ease(phaseLocalT, 5);
    brightness = 1.0f - 2f * Math.abs(EaseUtil.ease(phaseLocalT, 5) - 0.5f);
    switch (curAnimPhase) {
      case 3:
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.topSpineSpikeLights)
          colors[p.index] = color.getColor();
        break;
      case 2:
        //brightness = 1.0f - 2f * Math.abs(phaseLocalT - 0.5f);
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.upperRingFloods)
          colors[p.index] = color.getColor();
        break;
      case 1:
        //brightness = 1.0f - 2f * Math.abs(phaseLocalT - 0.5f);
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.lowerRingDownFloods)
          colors[p.index] = color.getColor();
        for (LXPoint p : SirsasanaModel.lowerRingUpFloods)
          colors[p.index] = color.getColor();
        break;
      case 0:
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.baseFloods)
          colors[p.index] = color.getColor();
        break;
    }
  }
}
