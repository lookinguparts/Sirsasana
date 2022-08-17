package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BooleanParameter;

import java.util.ArrayList;
import java.util.List;

public class MultiMask extends LXEffect {

  public List<LXPoint> blockPoints;

  BooleanParameter crown = new BooleanParameter("crown", true);
  BooleanParameter topRing = new BooleanParameter("topR", true);
  BooleanParameter lowerRingUp = new BooleanParameter("lowRU", true);
  BooleanParameter lowerRingDown = new BooleanParameter("lowRD", true);
  BooleanParameter canopy = new BooleanParameter("canopy", true);
  BooleanParameter ground = new BooleanParameter("ground", true);
  BooleanParameter bird = new BooleanParameter("bird", true);

  public MultiMask(LX lx) {
    super(lx);
    addParameter("crown", crown);
    addParameter("topR", topRing);
    addParameter("lowRU", lowerRingUp);
    addParameter("lowRD", lowerRingDown);
    addParameter("canopy", canopy);
    addParameter("ground", ground);
    addParameter("bird", bird);
    blockPoints = getBlockPoints(lx);
  }

  public List<LXPoint> getBlockPoints(LX lx) {
    List<LXPoint> points = new ArrayList<LXPoint>();
    for (LXPoint p : lx.getModel().points) {
      points.add(p);
    }
    if (crown.isOn())
      for (LXPoint p : SirsasanaModel.topCrownSpikeLights)
        points.remove(p);
    if (topRing.isOn())
      for (LXPoint p : SirsasanaModel.upperRingFloods)
        points.remove(p);
    if (lowerRingUp.isOn())
      for (LXPoint p : SirsasanaModel.lowerRingUpFloods)
        points.remove(p);
    if (lowerRingDown.isOn())
      for (LXPoint p : SirsasanaModel.lowerRingDownFloods)
        points.remove(p);
    if (canopy.isOn())
      for (LXPoint p : SirsasanaModel.canopyFloods)
        points.remove(p);
    if (ground.isOn())
      for (LXPoint p : SirsasanaModel.baseFloods)
        points.remove(p);
    if (bird.isOn())
      for (LXPoint p : SirsasanaModel.allBirdPoints)
        points.remove(p);
    return points;
  }

  public void run(double deltaMs, double damping) {
    blockPoints = getBlockPoints(lx);
    for (LXPoint p: blockPoints) {
      colors[p.index] = LXColor.rgba(0, 0, 0, 255);
    }
  }
}
