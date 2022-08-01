package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

@LXCategory(LXCategory.FORM)
public class Selector extends LXPattern {

  static public String[] sections = {
      "Spike",
      "Upper",
      "Lower Up",
      "Lower Down",
      "Ground",
      "All",
  };

  DiscreteParameter which = new DiscreteParameter("section", sections);
  ColorParameter color = new ColorParameter("color");
  BooleanParameter usePal = new BooleanParameter("palette", false);
  DiscreteParameter easeParam = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  DiscreteParameter swatch = new DiscreteParameter("swatch", 0, 0, 20);
  CompoundParameter speed = new CompoundParameter("speed", 0f, 0f, 10f);
  CompoundParameter perlinFreq = new CompoundParameter("perlFreq", 1f, 0f, 20f);

  EaseUtil ease = new EaseUtil(0);
  protected float curAngle = 0f;

  public Selector(LX lx) {
    super(lx);
    addParameter("section", which);
    addParameter("color", color);
    addParameter("palette", usePal);
    addParameter("ease", easeParam);
    addParameter("swatch", swatch);
    addParameter("speed", speed);
    addParameter("perlFreq", perlinFreq);
  }

  public int getColor(LXPoint p, double deltaMs) {
    int clr = color.getColor();
    if (usePal.getValueb()) {
      // If we are using the palette, lets spead the palette out from 0 to 360 degrees around the structure.
      float angle = (float)Math.atan2(p.z, p.x);
      angle = Math.abs(angle);
      
      angle = angle / (float)Math.PI;
      angle += curAngle;
      if (angle > 1.0) {
        angle = angle - 1.0f;
      }
      clr = Colors.getParameterizedPaletteColor(lx, swatch.getValuei(), angle, ease);
    }
    return clr;
  }

  @Override
  public void onActive() {
    super.onActive();
    curAngle = 0f;
  }

  public void run(double deltaMs) {
    ease.easeNum = easeParam.getValuei();
    if (ease.easeNum == 8) {
      ease.perlinFreq = perlinFreq.getValuef();
    }
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }
    if (which.getValuei() == 5) {
      for (LXPoint p : SirsasanaModel.allPoints) {
        colors[p.index] = getColor(p, deltaMs);
      }
    } else {
      switch (which.getValuei()) {
        case 0:
          for (LXPoint p : SirsasanaModel.topCrownSpikeLights)
            colors[p.index] = getColor(p, deltaMs);
          break;
        case 1:
          for (LXPoint p : SirsasanaModel.upperRingFloods)
            colors[p.index] = getColor(p, deltaMs);
          break;
        case 2:
          for (LXPoint p : SirsasanaModel.lowerRingUpFloods)
            colors[p.index] = getColor(p, deltaMs);
          break;
        case 3:
          for (LXPoint p : SirsasanaModel.lowerRingDownFloods)
            colors[p.index] = getColor(p, deltaMs);
          break;
        case 4:
          for (LXPoint p : SirsasanaModel.baseFloods)
            colors[p.index] = getColor(p, deltaMs);
          break;
      }
    }
    curAngle += speed.getValuef() * (deltaMs/1000f);
    if (curAngle > 1f) {
      curAngle -= 1f;
    }
  }
}
