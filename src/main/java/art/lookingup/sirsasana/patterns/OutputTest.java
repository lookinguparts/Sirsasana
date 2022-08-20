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
public class OutputTest extends LXPattern {

  static public String[] sections = {
      "Spike",
      "Upper",
      "Lower Up",
      "Lower Down",
      "Ground",
      "Canopy",
      "Birds",
      "All",
  };

  DiscreteParameter which = new DiscreteParameter("section", sections);
  CompoundParameter group = new CompoundParameter("group", -1, -1, 5).setDescription("Light group within the section.");
  ColorParameter color = new ColorParameter("color");
  BooleanParameter usePal = new BooleanParameter("palette", false);
  DiscreteParameter easeParam = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  DiscreteParameter swatch = new DiscreteParameter("swatch", 0, 0, 20);
  CompoundParameter speed = new CompoundParameter("speed", 0f, 0f, 10f);
  CompoundParameter perlinFreq = new CompoundParameter("perlFreq", 1f, 0f, 20f);
  BooleanParameter iterate = new BooleanParameter("iterate", false);
  CompoundParameter iDur = new CompoundParameter("iDur", 200f, 0f, 20000f);

  int currentLight = 0;
  float currentLightTime = 0f;

  EaseUtil ease = new EaseUtil(0);
  protected float curAngle = 0f;

  public OutputTest(LX lx) {
    super(lx);
    addParameter("section", which);
    addParameter("group", group);
    addParameter("color", color);
    addParameter("palette", usePal);
    addParameter("ease", easeParam);
    addParameter("swatch", swatch);
    addParameter("speed", speed);
    addParameter("perlFreq", perlinFreq);
    addParameter("iterate", iterate);
    addParameter("iDur", iDur);

    color.brightness.setValue(100f);
  }

  public int getColor(LXPoint p, int secIndex, double deltaMs) {
    int clr = color.getColor();
    if (iterate.isOn() && currentLight != secIndex)
      return LXColor.BLACK;

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
    int secIndex = 0;
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }
    if (which.getValuei() == 7) {
      for (LXPoint p : SirsasanaModel.allPoints) {
        colors[p.index] = getColor(p, secIndex++, deltaMs);
      }
    } else {
      switch (which.getValuei()) {
        case 0:
          for (LXPoint p : SirsasanaModel.topCrownSpikeLightsSorted)
            colors[p.index] = getColor(p, secIndex++, deltaMs);
          break;
        case 1:
          for (LXPoint p : SirsasanaModel.upperRingFloodsSorted)
            colors[p.index] = getColor(p, secIndex++, deltaMs);
          break;
        case 2:
          for (LXPoint p : SirsasanaModel.lowerRingUpFloodsSorted)
            colors[p.index] = getColor(p, secIndex++, deltaMs);
          break;
        case 3:
          for (LXPoint p : SirsasanaModel.lowerRingDownFloodsSorted)
            colors[p.index] = getColor(p, secIndex++, deltaMs);
          break;
        case 4:
          if ((int)group.getValue() == -1 ) {
            for (LXPoint p : SirsasanaModel.baseFloodsSorted)
              colors[p.index] = getColor(p, secIndex++, deltaMs);
          } else {
            for (LXPoint p : SirsasanaModel.groundFloodGroups.get((int)Math.round(group.getValue()) % 3))
              colors[p.index] = getColor(p, secIndex++, deltaMs);
          }
          break;
        case 5:
          if ((int)group.getValue() == -1 ) {
            for (LXPoint p : SirsasanaModel.canopyFloodsSorted)
              colors[p.index] = getColor(p, secIndex++, deltaMs);
          } else {
            for (LXPoint p : SirsasanaModel.canopyFloodGroups.get((int)Math.round(group.getValue()) % 6))
              colors[p.index] = getColor(p, secIndex++, deltaMs);
          }
          break;
        case 6:
          for (LXPoint p : SirsasanaModel.allBirdPoints) {
            colors[p.index] = getColor(p, secIndex++, deltaMs);
          }
          break;
      }
    }
    curAngle += speed.getValuef() * (deltaMs/1000f);
    if (curAngle > 1f) {
      curAngle -= 1f;
    }
    if (iterate.isOn()) {
      currentLightTime += deltaMs;
      if (currentLightTime > iDur.getValuef()) {
        currentLightTime = 0f;
        incrementCurrentLight();
      }
    }
  }

  /**
   * For iterating the lights in a section.  We need to increment the current light index and it needs to
   * wrap appropriately given the number of lights in a section.
   */
  public void incrementCurrentLight() {
    currentLight++;
    if (currentLight >= getNumLightsInSection())
      currentLight = 0;
  }

  public int getNumLightsInSection() {
    switch (which.getValuei()) {
      case 0:
        return SirsasanaModel.topCrownSpikeLights.size();
      case 1:
        return SirsasanaModel.upperRingFloods.size();
      case 2:
        return SirsasanaModel.lowerRingUpFloods.size();
      case 3:
        return SirsasanaModel.lowerRingDownFloods.size();
      case 4:
        return SirsasanaModel.baseFloods.size();
      case 5:
        return SirsasanaModel.canopyFloods.size();
      case 6:
        return SirsasanaModel.allBirdPoints.size();
      case 7:
        return SirsasanaModel.allPoints.size();
    }
    return 1;
  }
}
