package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.AnimUtils;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;

// TODO(tracy): Implement speed for not requiring an LFO?
public class Vertical extends LXPattern {

  public String waves[] = { "tri", "step", "stepr", "square"};

  CompoundParameter pos = new CompoundParameter("pos", 0.5f, -2f, 2f).setDescription("Position of center");
  DiscreteParameter wave = new DiscreteParameter("wave", waves);
  CompoundParameter slope = new CompoundParameter("slope", 1f, 0f, 30f).setDescription("Slope if applicable");
  CompoundParameter width = new CompoundParameter("width", 0.1f, 0, 4f).setDescription("Width of square wave");
  CompoundParameter speed = new CompoundParameter("speed", 1f, 0f, 30f).setDescription("Sweep speed");
  CompoundParameter bgintensity = new CompoundParameter("bgi", 0, 0, 1 ).setDescription("Background Intensity");
  ColorParameter color = new ColorParameter("color");
  CompoundParameter maxIntensity = new CompoundParameter("maxi", 1f, 0f, 1f).setDescription("Max intensity");

  BooleanParameter usePal = new BooleanParameter("usePal", false);
  DiscreteParameter easeParam = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);
  DiscreteParameter swatch = new DiscreteParameter("swatch", 0, 0, 20);
  CompoundParameter perlinFreq = new CompoundParameter("perlFreq", 1f, 0f, 20f);

  public EaseUtil ease = new EaseUtil(0);

  public Vertical(LX lx) {
    super(lx);
    addParameter("pos", pos);
    addParameter("wave", wave);
    addParameter("slope", slope);
    addParameter("width", width);

    addParameter("speed", speed);
    addParameter("bgi", bgintensity);
    addParameter("maxi", maxIntensity);
    addParameter("color", color);
    addParameter("usePal", usePal);
    addParameter("ease", easeParam);
    addParameter("swatch", swatch);
    addParameter("perlFreq", perlinFreq);
  }

  public int getColor(LXPoint p, float t) {
    int clr = color.getColor();
    clr = Colors.getWeightedColor(clr, ease.ease(t));

    if (usePal.getValueb()) {
      clr = Colors.getParameterizedPaletteColor(lx, swatch.getValuei(), t, ease);
    }
    return clr;
  }

  public void run(double deltaMs) {
    for (LXPoint p : lx.getModel().points) {
      float t = (p.y - lx.getModel().yMin) / (lx.getModel().yMax - lx.getModel().yMin);
      float val = 0f;
      if (wave.getValuei() == 0) {
        val =AnimUtils.triangleWave(pos.getValuef(), slope.getValuef(), t);
      } else if (wave.getValuei() == 1) {
        val = AnimUtils.stepDecayWave(pos.getValuef(), width.getValuef(), slope.getValuef(), t, true);
      } else if (wave.getValuei() == 2) {
        val = AnimUtils.stepDecayWave(pos.getValuef(), width.getValuef(), slope.getValuef(), t, false);
      } else if (wave.getValuei() == 3) {
        val = AnimUtils.squareWave(pos.getValuef(), width.getValuef(), t);
      }
      val = ease.ease(val);
      if (val < bgintensity.getValuef())
        val = bgintensity.getValuef();
      int clr = getColor(p, val);
      val = val * maxIntensity.getValuef();
      clr = Colors.getWeightedColor(clr, val);
      colors[p.index] = clr;
    }
  }
}
