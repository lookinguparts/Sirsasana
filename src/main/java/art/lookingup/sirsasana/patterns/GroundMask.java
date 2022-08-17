package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class GroundMask extends LXEffect {

  public List<LXPoint> allPointsButGround;

  public GroundMask(LX lx) {
    super(lx);
    allPointsButGround = getAllPointsButGround(lx);
  }

  public List<LXPoint> getAllPointsButGround(LX lx) {
    List<LXPoint> points = new ArrayList<LXPoint>();
    for (LXPoint p : lx.getModel().points) {
      points.add(p);
    }
    for (LXPoint p : SirsasanaModel.baseFloods) {
      points.remove(p);
    }
    return points;
  }

  public void run(double deltaMs, double damping) {
    for (LXPoint p: allPointsButGround) {
      colors[p.index] = LXColor.rgba(0, 0, 0, 255);
    }
  }
}
