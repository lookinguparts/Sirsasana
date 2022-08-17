package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.Bird;
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
public class FlappyBird extends LXPattern {
  // CompoundParameter speed = new CompoundParameter("speed", 1f, 0f, 30f).setDescription("Sweep speed");
  // CompoundParameter angleWidth = new CompoundParameter("angleW", 45, 0, 360).setDescription("Angle width");

  public FlappyBird(LX lx) {
    super(lx);
    // addParameter("speed", speed);
    // addParameter("angleW", angleWidth);
  }

  public void run(double deltaMs) {
      for (Bird bird : SirsasanaModel.birds) {
	  for (LXPoint p : bird.side1Points) {
	      colors[p.index] = Colors.VIOLET;
	  }
	  for (LXPoint p : bird.side2Points) {
	      colors[p.index] = Colors.BLUE;
	  }
      }
  }
}
