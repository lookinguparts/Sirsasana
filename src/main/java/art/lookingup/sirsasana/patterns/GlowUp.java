package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

public class GlowUp extends AnimT {
  ColorParameter color = new ColorParameter("color");
  CompoundParameter freq = new CompoundParameter("freq", 1f, 0f, 5f).setDescription("sine ease frequency");
  DiscreteParameter ease = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);

  public GlowUp(LX lx) {
    super(lx);
    addParameter(color);
    registerPhase("base",1f, 3f, "Base");
    registerPhase("lower", 1f, 3f, "Lower");
    registerPhase("upper", 1f, 3f, "Upper");
    registerPhase("spike", 1f, 3f, "Spike");
    color.brightness.setValue(100.0);
    addParameter(ease);
    addParameter(freq);
  }

  public void renderPhase(int curAnimPhase, float phaseLocalT) {
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }
    float brightness = 1.0f - 2f * Math.abs(EaseUtil.ease(phaseLocalT, ease.getValuei()) - 0.5f);
    // Configurable sine wave easing is special and can take an additional configuration parameter.
    if (ease.getValuei() == 6) {
      brightness = 1.0f - 2f * Math.abs(EaseUtil.ease6(phaseLocalT, freq.getValuef()) - 0.5f);
    }
    switch (curAnimPhase) {
      case 3:
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.topCrownSpikeLights)
          colors[p.index] = color.getColor();
        break;
      case 2:
        color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.upperRingFloods)
          colors[p.index] = color.getColor();
        break;
      case 1:
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
