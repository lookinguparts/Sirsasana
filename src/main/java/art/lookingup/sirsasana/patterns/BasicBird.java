package art.lookingup.sirsasana.patterns;

import art.lookingup.sirsasana.SirsasanaModel;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;

public class BasicBird extends BirdBase {

  double totalSecs = 0f;

  public BasicBird(LX lx) {
    super(lx);
  }

  @Override
  public void onActive() {
    totalSecs = 0f;
  }

  @Override
  public void afterRender(double deltaMs) {
    totalSecs += deltaMs / 1000f;
  }

  public void renderBirdSinging(int[] colors, SirsasanaModel.Bird bird, double deltaMs) {
    for (LXPoint p : bird.points) {
      float v = 0.5f + 0.5f * (float)Math.sin(totalSecs * 10f);
      colors[p.index] = LXColor.gray(v * 100);
    }
  }

  public void renderBirdIdle(int[] colors, SirsasanaModel.Bird bird, double deltaMs) {
    for (LXPoint p : bird.points) {
      float v = 0.2f;
      colors[p.index] = LXColor.gray(v * 100);
    }
  }
}
