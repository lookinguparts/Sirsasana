package art.lookingup.sirsasana.patterns;

import art.lookingup.colors.Colors;
import heronarts.lx.LX;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

import java.util.List;

public class GroundLevel extends LXEffect {

  public List<LXPoint> blockPoints;

  CompoundParameter level = new CompoundParameter("level", 1f, 0f, 1f).setDescription("Adjust power level");

  public GroundLevel(LX lx) {
    super(lx);
    addParameter("level", level);
    blockPoints = GroundMask.getBlockPoints(lx);
  }

  public void run(double deltaMs, double damping) {
    for (LXPoint p: lx.getModel().points) {
      if (!blockPoints.contains(p))
        colors[p.index] = Colors.getWeightedColor(colors[p.index], level.getValuef());
    }
  }
}
