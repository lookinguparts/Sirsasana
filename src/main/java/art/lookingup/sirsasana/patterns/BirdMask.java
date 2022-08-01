
package art.lookingup.sirsasana.patterns;
    import art.lookingup.sirsasana.SirsasanaModel;
    import heronarts.lx.LX;
    import heronarts.lx.color.LXColor;
    import heronarts.lx.effect.LXEffect;
    import heronarts.lx.model.LXPoint;

public class BirdMask extends LXEffect {

  public BirdMask(LX lx) {
    super(lx);

  }

  public void run(double deltaMs, double damping) {
    for (LXPoint p: SirsasanaModel.allFloodsNoBirds) {
      colors[p.index] = LXColor.rgba(0, 0, 0, 255);
    }
  }
}
