package art.lookingup.sirsasana.patterns;

import art.lookingup.EaseUtil;
import art.lookingup.colors.Colors;
import art.lookingup.sirsasana.Bird;
import art.lookingup.sirsasana.SirsasanaApp;
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
import processing.core.PImage;
import java.util.ArrayList;

@LXCategory(LXCategory.FORM)
public class FlappyBird extends LXPattern {
  // CompoundParameter speed = new CompoundParameter("speed", 1f, 0f, 30f).setDescription("Sweep speed");
  // CompoundParameter angleWidth = new CompoundParameter("angleW", 45, 0, 360).setDescription("Angle width");

    public static class position {
	int x;
	int y;
	int r;

	position(int x, int y, int r) {
	    this.x = x;
	    this.y = y;
	    this.r = r;
	}
    };

  PImage ctexture;
  ArrayList<position> positions;

  public FlappyBird(LX lx) {
    super(lx);

    // Image is 400x400
    this.ctexture = SirsasanaApp.pApplet.loadImage("lch-disc-level=0.60-sat=1.00.png");    
    this.ctexture.loadPixels();

    this.positions = new ArrayList<position>();
    
    for (Bird bird : SirsasanaModel.birds) {
	this.positions.add(new position(200, 200, 150));
    }

    // addParameter("speed", speed);
    // addParameter("angleW", angleWidth);
  }

  public void run(double deltaMs) {
      for (Bird bird : SirsasanaModel.birds) {
	  for (int i = 0; i < 13; i++) {
	      double c = Math.cos(2 * Math.PI * (double)i / 13);
	      double s = Math.sin(2 * Math.PI * (double)i / 13);

	      LXPoint l = bird.side1Points.get(i);
	      LXPoint r = bird.side2Points.get(i);

	      int x = (int)(200 + 150 * c);
	      int y = (int)(200 + 150 * s);

	      colors[l.index] = this.ctexture.pixels[y*400+x];
	      colors[r.index] = this.ctexture.pixels[y*400+x];
	  }
      }
  }
}
