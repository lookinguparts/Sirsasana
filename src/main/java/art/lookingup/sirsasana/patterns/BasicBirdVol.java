package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.Bird;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;

public class BasicBirdVol extends BirdVol {

  ColorParameter color = new ColorParameter("color");
  BooleanParameter usePal = new BooleanParameter("usePal", false);
  CompoundParameter bgintensity = new CompoundParameter("bgi", 0, 0, 1 ).setDescription("Background Intensity");
  CompoundParameter maxIntensity = new CompoundParameter("maxi", 1f, 0f, 1f).setDescription("Max intensity");
  CompoundParameter sparkle = new CompoundParameter("sparkle", 0,0,1).setDescription("Sparkle Effect");
  DiscreteParameter easeParam = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  DiscreteParameter swatch = new DiscreteParameter("swatch", 0, 0, 20);
  CompoundParameter perlinFreq = new CompoundParameter("perlFreq", 1f, 0f, 20f);
  CompoundParameter idleIntensity = new CompoundParameter("idlei", 0.2f, 0f, 1f).setDescription("Idle intensity");
  CompoundParameter volScale = new CompoundParameter("volScl", 1f, 0f, 5f).setDescription("Volume feedback scale");

  double totalSecs = 0f;
  EaseUtil ease = new EaseUtil(0);

  public BasicBirdVol(LX lx) {

    super(lx);
    addParameter("color", color);
    addParameter("bgi", bgintensity);
    addParameter("maxi", maxIntensity);
    addParameter("sparkle", sparkle);
    addParameter("usePal", usePal);
    addParameter("ease", easeParam);
    addParameter("swatch", swatch);
    addParameter("perlFreq", perlinFreq);
    addParameter("idlei", idleIntensity);
    addParameter("volScale", volScale);
    color.brightness.setValue(100);

  }

  @Override
  public void onActive() {
    totalSecs = 0f;
  }

  @Override
  public void afterRender(double deltaMs) {
    totalSecs += deltaMs / 1000f;
  }

  public void renderBirdSinging(int[] colors, Bird bird, double deltaMs) {
    ease.easeNum = easeParam.getValuei();
    if (ease.easeNum == 8) {
      ease.perlinFreq = perlinFreq.getValuef() * bird.getVolume() * volScale.getValuef();
    } else if (ease.easeNum == 6) {
      ease.freq = perlinFreq.getValuef() * bird.getVolume() * volScale.getValuef();
    }
    for (LXPoint p : bird.points) {
      int clr = getColor(p, bird);
      //float intensity = (((1f - sparkle.getValuef() ) + sparkle.getValuef() * (float) Math.random())* intensity);
      // intensity = intensity * maxIntensity.getValuef();
      clr = Colors.getWeightedColor(clr, maxIntensity.getValuef());
      colors[p.index] = clr;
      //float v = 0.5f + 0.5f * (float)Math.sin(totalSecs * 10f);
      //colors[p.index] = LXColor.gray(v * 100);
    }
  }

  public int getColor(LXPoint p, Bird b) {
    return getColor(p, ease.ease(pointT(p, b)));
  }

  public int getColor(LXPoint p, float t) {
    int clr = color.getColor();
    clr = Colors.getWeightedColor(clr, ease.ease(t));

    if (usePal.getValueb()) {
      clr = Colors.getParameterizedPaletteColor(lx, swatch.getValuei(), t, ease);
    }
    return clr;
  }

  public float pointT(LXPoint p, Bird b) {
    // First point of a bird is the low point, last point on the first side is the high point.
    float maxY = b.side1Points.get(b.side1Points.size()-1).y;
    float minY = b.side1Points.get(0).y;
    float range = maxY - minY;
    float yOffset = p.y - minY;
    float t = yOffset / range;
    return t;
  }

  public void renderBirdIdle(int[] colors, Bird bird, double deltaMs) {
    ease.easeNum = easeParam.getValuei();
    if (ease.easeNum == 8) {
      ease.perlinFreq = 1f;
    } else if (ease.easeNum == 6) {
      ease.freq = 1f;
    }
    for (LXPoint p : bird.points) {
      int clr = getColor(p, bird);
      //float intensity = (((1f - sparkle.getValuef() ) + sparkle.getValuef() * (float) Math.random())* intensity);
      // intensity = intensity * maxIntensity.getValuef();
      clr = Colors.getWeightedColor(clr, idleIntensity.getValuef());
      colors[p.index] = clr;
      //float v = 0.5f + 0.5f * (float)Math.sin(totalSecs * 10f);
      //colors[p.index] = LXColor.gray(v * 100);
    }
  }
}
