package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;


/*
* One at a time, glows each layer the specified color from bottom to top
*/
public class GlowUp extends AnimT {
  ColorParameter color = new ColorParameter("color");
  CompoundParameter freq = new CompoundParameter("freq", 1f, 0f, 5f).setDescription("sine ease frequency");
  DiscreteParameter ease = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  CompoundParameter maxIntensity = new CompoundParameter("maxI", 1f, 0f, 1f).setDescription("Max intensity");
  CompoundParameter bgIntensity = new CompoundParameter("bgI", 0.5f, 0f, 1f).setDescription("Background intensity");

  public GlowUp(LX lx) {
    super(lx);
    addParameter("color", color);
    // 0
    registerPhase("base",1f, 3f, "Base");
    // 1
    registerPhase("canopy",1f, 3f, "Canopy");
    // 2
    registerPhase("lower_ring", 1f, 3f, "Lower Ring");
    // 3
    registerPhase("upper_ring", 1f, 3f, "Upper Ring");
    // 4
    registerPhase("crown", 1f, 3f, "Crown");
    color.brightness.setValue(100.0);
    addParameter("maxI", maxIntensity);
    addParameter("bgI", bgIntensity);
    addParameter("ease", ease);
    addParameter("freq", freq);
  }

  public void renderPhase(int curAnimPhase, float phaseLocalT) {
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = Colors.getWeightedColor(color.getColor(), bgIntensity.getValuef());
    }
    float brightness = 1.0f - 2f * Math.abs(EaseUtil.ease(phaseLocalT, ease.getValuei()) - 0.5f);

    // Configurable sine wave easing is special and can take an additional configuration parameter.
    if (ease.getValuei() == 6) {
      brightness = 1.0f - 2f * Math.abs(EaseUtil.ease6(phaseLocalT, freq.getValuef()) - 0.5f);
    }

    if (brightness < bgIntensity.getValuef())
      brightness = bgIntensity.getValuef();

    brightness = maxIntensity.getValuef() * brightness;

    int clr = Colors.getWeightedColor(color.getColor(), brightness);
    switch (curAnimPhase) {
      case 4:
        //color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.topCrownSpikeLights)
          colors[p.index] = clr;
        break;
      case 3:
        //color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.upperRingFloods)
          colors[p.index] = clr;
        break;
      case 2:
        //color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.lowerRingDownFloods)
          colors[p.index] = clr;
        for (LXPoint p : SirsasanaModel.lowerRingUpFloods)
          colors[p.index] = clr;
        break;
      case 1:
        //color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.canopyFloods)
          colors[p.index] = clr;
        break;
      case 0:
        //color.brightness.setValue(100f * brightness);
        for (LXPoint p : SirsasanaModel.baseFloods)
          colors[p.index] = clr;
        break;
    }
  }
}
