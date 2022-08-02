package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;

public class BirdGlow extends LXPattern {

  int currentIndex = 0;
  double timePerBirdMs = 2000;
  double currentElapsed = 0;

  public BirdGlow(LX lx) {
    super(lx);
  }

  public void run(double deltaMs) {
    if (currentElapsed > timePerBirdMs) {
      currentIndex++;
      currentElapsed = 0.0;
      if (currentIndex >= SirsasanaModel.birds.size())
        currentIndex = 0;
    }
    for (SirsasanaModel.Bird bird : SirsasanaModel.birds) {
      if (bird.id == currentIndex) {
        // todo - make this slightly nicer, by adding ease
        for (LXPoint point : bird.points) {
          colors[point.index] = LXColor.WHITE;
        }
      } else {
        for (LXPoint point : bird.points) {
          colors[point.index] = LXColor.BLACK;
        }
      }
      currentElapsed += deltaMs;
    }
  }
}
