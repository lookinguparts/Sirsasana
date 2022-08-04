package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.ColorParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import art.lookingup.sirsasana.SirsasanaModel.Bird;

public class BirdGlow extends AnimT {
  ColorParameter color = new ColorParameter("color");
  CompoundParameter freq = new CompoundParameter("freq", 1f, 0f, 5f).setDescription("sine ease frequency");
  DiscreteParameter ease = new DiscreteParameter("ease", 0, 0, EaseUtil.MAX_EASE + 1);

  public BirdGlow(LX lx) {
    super(lx);

    addParameter(color);

    for (Bird bird : SirsasanaModel.birds)  {
      registerPhase("bird" + bird.id, 1f, 3f, "Bird " + bird.id);
    }

    color.brightness.setValue(100.0);

    addParameter(ease);
    addParameter(freq);
  }

  @Override
  public void renderPhase(int curAnimPhase, float phaseLocalT) {
    float brightness = 1.0f - 2f * Math.abs(EaseUtil.ease(phaseLocalT, ease.getValuei()) - 0.5f);

    // Configurable sine wave easing is special and can take an additional configuration parameter.
    if (ease.getValuei() == 6) {
      brightness = 1.0f - 2f * Math.abs(EaseUtil.ease6(phaseLocalT, freq.getValuef()) - 0.5f);
    }

    // change this to only update all the birds
    for (LXPoint p : SirsasanaModel.allPoints) {
      colors[p.index] = LXColor.BLACK;
    }

    Bird birdToUpdate = SirsasanaModel.birds.get(curAnimPhase);

    color.brightness.setValue(100f * brightness);

    for (LXPoint p : birdToUpdate.points) {
      colors[p.index] = color.getColor();
    }
  }
}
