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
public class Sweep extends FPSPattern {
  CompoundParameter speed = new CompoundParameter("speed", 1f, 0f, 30f).setDescription("Sweep speed");
  CompoundParameter angleWidth = new CompoundParameter("angleW", 45, 0, 360).setDescription("Angle width");
  CompoundParameter bgintensity = new CompoundParameter("bgi", 0, 0, 1 ).setDescription("Background Intensity");
  ColorParameter color = new ColorParameter("color");
  CompoundParameter maxIntensity = new CompoundParameter("maxi", 1f, 0f, 1f).setDescription("Max intensity");

  CompoundParameter sparkle = new CompoundParameter("sparkle", 0,0,1).setDescription("Sparkle Effect");
  BooleanParameter usePal = new BooleanParameter("usePal", false);
  DiscreteParameter easeParam = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  DiscreteParameter swatch = new DiscreteParameter("swatch", 0, 0, 20);
  CompoundParameter perlinFreq = new CompoundParameter("perlFreq", 1f, 0f, 20f);
  BooleanParameter bgIntPalStop = new BooleanParameter("bgIPalStp", true).setDescription("Use the bg intensity as palette min");

  EaseUtil ease = new EaseUtil(0);
  float currentAngle = 0f;

  public Sweep(LX lx) {
    super(lx);
    addParameter("fps", fpsKnob);
    addParameter("speed", speed);
    addParameter("angleW", angleWidth);
    addParameter("bgi", bgintensity);
    addParameter("maxi", maxIntensity);
    addParameter("sparkle",sparkle);
    addParameter("color", color);
    addParameter("usePal", usePal);
    addParameter("bgIPalStp", bgIntPalStop);
    addParameter("ease", easeParam);
    addParameter("swatch", swatch);
    addParameter("perlFreq", perlinFreq);

    color.brightness.setValue(100.0);
  }

  public void onActive() {
    super.onActive();
    currentAngle = 0f;
  }

  public float angle(LXPoint p) {
    return 360f * (float)(p.azimuth/(Math.PI * 2f));
  }

  public int getColor(LXPoint p, float t) {
    int clr = color.getColor();
    clr = Colors.getWeightedColor(clr, ease.ease(t));

    if (usePal.getValueb()) {
      clr = Colors.getParameterizedPaletteColor(lx, swatch.getValuei(), t, ease);
    }
    return clr;
  }

  public boolean isinRange(float pointAngle) {
    if ( pointAngle > currentAngle - angleWidth.getValuef() /2f && pointAngle < currentAngle + angleWidth.getValue()/ 2f)
      return true;
    float overlap = currentAngle + angleWidth.getValuef() / 2f- 360f;
    if ( overlap > 0f)
      if (pointAngle < overlap)
        return true;
    float underlap = currentAngle - angleWidth.getValuef()/ 2f;
    if (underlap < 0 )
      if (pointAngle > 360f + underlap)
        return true;
    return false;
  }
  public float intensityat(float pointangle) {
    float distancefromhead = distancefromhead(pointangle);
    if (distancefromhead > 1) {
      if (!usePal.isOn() || (usePal.isOn() && !bgIntPalStop.isOn())) {
        return ease.ease(bgintensity.getValuef());
      } else {
        return ease.ease(0f); // If we are using the palette we will use this to grab lerp'd swatch color.
      }
    }
    return ease.ease(bgintensity.getValuef() + (1- distancefromhead) * ( 1 - bgintensity.getValuef()));

  }

  /**
   * @param pointAngle
   * @return 1 to 0 based on distance from the head where 1 is equivalent to angleWidth.
   */
  public float distancefromhead(float pointAngle){
    float headPos = currentAngle;
    float distBack = pointAngle - headPos;
    if (headPos - pointAngle < 0) {
      // pointAngle -= 360f;  // Change proint angle from 350 to -10 for example
    }
    float distance = Math.abs(headPos - pointAngle);

    float wrappedDistance = Math.abs(headPos + 360f - pointAngle);
    float wrappedDistance2 = Math.abs(pointAngle + 360f - headPos);

    distance = Math.min(distance, wrappedDistance);
    distance = Math.min(distance, wrappedDistance2);

    // Now handle the case where we cross the 0 boundary again. So headPos = 10 and pointAngle is 350.
    // Currently, that gives -340 but should be 20.  So if we add 360 to -340 we get 20.  But what about
    // headPos = 340 and pointAngle = 350, we get -10 but it should be

    return distance / angleWidth.getValuef();
  }

  public void renderFrame(double deltaMs) {
    ease.easeNum = easeParam.getValuei();
    if (ease.easeNum == 8) {
      ease.perlinFreq = perlinFreq.getValuef();
    }
    for (LXPoint p : lx.getModel().points) {
      colors[p.index] = LXColor.BLACK;
    }
    for (LXPoint p : SirsasanaModel.allPoints) {
      float angleDegrees = angle(p);
      float intensity = intensityat(angleDegrees);
      int clr = getColor(p, intensity);
      // If we are using a palette, we also need to apply our brightness fall-off.
      // TODO(tracy): Move this into getColor().
      if (usePal.isOn()) {
        if (intensity < bgintensity.getValuef()) {
          intensity = bgintensity.getValuef();
        }
        //clr = Colors.getWeightedColor(clr, intensity);
      }
      intensity = (((1f - sparkle.getValuef() ) + sparkle.getValuef() * (float) Math.random())*intensity);
      intensity = intensity * maxIntensity.getValuef();
      clr = Colors.getWeightedColor(clr, intensity);
      colors[p.index] = clr;
    }
    currentAngle += speed.getValuef();
    if (currentAngle > 360f)
      currentAngle  -= 360f;
  }
}
