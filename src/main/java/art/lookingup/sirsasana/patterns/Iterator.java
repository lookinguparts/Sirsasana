package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;

public class Iterator extends LXPattern {

  int currentIndex = 0;
  double timePerLedMs = 500;
  double currentElapsed = 0;

  public Iterator(LX lx) {
    super(lx);
  }

  public void run(double deltaMs) {
    if (currentElapsed > timePerLedMs) {
      currentIndex++;
      currentElapsed = 0.0;
      if (currentIndex >= SirsasanaModel.allPoints.size())
        currentIndex = 0;
    }
    for (LXPoint p : SirsasanaModel.allPoints) {
      if (p.index == currentIndex) {
        colors[p.index] = LXColor.WHITE;
      } else {
        colors[p.index] = LXColor.BLACK;
      }
      currentElapsed += deltaMs;
    }
  }
}
