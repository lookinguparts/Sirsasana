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
import java.util.Random;

@LXCategory(LXCategory.FORM)
public class FlappyBird extends LXPattern {
    public static final double radius = 200.0;

    public static final int centerX = 200;
    public static final int centerY = 200;

    public static final double minArc = Math.PI / 8.0;
    public static final double maxArc = Math.PI / 2.0;

    public static final double minRad = 0.2;
    public static final double maxRad = 0.8;

    public static final double minJitter = 0.75;
    public static final double maxJitter = 1.25;
    
    static double newArc(Random rnd) {
	double value = minArc + rnd.nextDouble() * (maxArc-minArc);
	if (rnd.nextBoolean()) {
	    return -value;
	}
	return value;
    }

    static double newAngle(Random rnd) {
	return rnd.nextDouble() * 2 * Math.PI;
    }

    static double newRad(Random rnd) {
	return minRad + rnd.nextDouble() * (maxRad-minRad);
    }

    static double newJitter(Random rnd) {
	return minJitter + rnd.nextDouble() * (maxJitter-minJitter);
    }
    
    public static final int segmentations[][] = {
	{ 6, 7 },
	{ 5, 8 },
	{ 4, 9 },

	{ 7, 6 },
	{ 8, 5 },
	{ 9, 4 },

	{ 3, 3, 7 },
	{ 3, 7, 3 },
	{ 3, 4, 6 },
	{ 4, 3, 6 },
	{ 6, 3, 4 },
	{ 4, 5, 4 },
    };

    public static class segment {
	int count;
	double angle;
	double arc;
	double rad;
	double jitter;

	segment(int count, double angle, double arc, double rad, double jitter) {
	    this.count = count;
	    this.angle = angle;
	    this.arc = arc;
	    this.rad = rad;
	    this.jitter = jitter;
	}
    };
    
    public class coloring {
	ArrayList<segment> segments;
	double wobble;

	coloring() {
	    int s = rnd.nextInt(segmentations.length);
	    int segs[] = segmentations[s];
    
	    this.segments = new ArrayList<segment>(segs.length);
	    this.wobble = newJitter(rnd);

	    for (int i = 0; i < segs.length; i++) {
		this.segments.add(new segment(segs[i], newAngle(rnd), newArc(rnd), newRad(rnd), newJitter(rnd)));
	    }
	}

	int wobbleTime() {
	    return (int)(simtime * this.wobble * 5) % 6;
	}

	PImage leftTexture() {
	    int value = wobbleTime();
	    if (value < 4) {
		return ctexture[value];
	    } else {
		return ctexture[6-value];
	    }
	}

	PImage rightTexture() {
	    int value = wobbleTime();
	    if (value < 4) {
		return ctexture[3-value];
	    } else {
		return ctexture[value-3];
	    }
	}
    };

  Random rnd;
  PImage ctexture[];
  ArrayList<coloring> colorings;

  public FlappyBird(LX lx) {
    super(lx);

    this.rnd = new Random();
    this.ctexture = new PImage[4];
    this.ctexture[0] = SirsasanaApp.pApplet.loadImage("lch-disc-level=0.60-sat=1.00.png");
    this.ctexture[1] = SirsasanaApp.pApplet.loadImage("lch-disc-level=0.65-sat=1.00.png");
    this.ctexture[2] = SirsasanaApp.pApplet.loadImage("lch-disc-level=0.70-sat=1.00.png");
    this.ctexture[3] = SirsasanaApp.pApplet.loadImage("lch-disc-level=0.75-sat=1.00.png");
    for (PImage img : this.ctexture) {
	img.loadPixels();
    }

    this.colorings = new ArrayList<coloring>();

    for (Bird bird : SirsasanaModel.birds) {
	this.colorings.add(new coloring());
    }
  }

  public static final double timescale = 1/2500.0;
  double simtime;

  public void run(double deltaMs) {
      simtime += deltaMs * timescale;

      for (int bno = 0; bno < SirsasanaModel.birds.size(); bno++) {
	  Bird bird = SirsasanaModel.birds.get(bno);
	  coloring c = colorings.get(bno);

	  int pno = 0;

	  for (segment seg : c.segments) {
	      double per = seg.arc / (double)(seg.count-1);
	      double shimmer = (maxArc+minArc)/2.0*Math.sin(seg.jitter*simtime);
		  
	      for (int part = 0; part < seg.count; part++, pno++) {

		  double a = seg.angle + part * per + shimmer;

		  double cos = Math.cos(a);
		  double sin = Math.sin(a);

		  LXPoint l = bird.side1Points.get(pno);

		  LXPoint r = bird.side2Points.get(12-pno);

		  double rad = seg.rad;

		  double dx = cos * rad * radius;
		  double dy = sin * rad * radius;

		  int x = (int)(centerX + dx);
		  int y = (int)(centerY + dy);

		  colors[l.index] = c.leftTexture().pixels[y*400+x];
		  colors[r.index] = c.rightTexture().pixels[y*400+x];
	      }
	  }
      }
  }
}
