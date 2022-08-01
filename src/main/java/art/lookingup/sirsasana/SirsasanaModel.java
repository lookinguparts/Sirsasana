package art.lookingup.sirsasana;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import java.util.*;
import java.util.logging.Logger;

/**
 * Sirsasana Model
 *
 * Lots of floods, some birds.
 *
 * Main structure:
 *   - 6 tines shaped like a long 'X'
 *   - Each tine is connected to 4 other tines.
 *   - There are 12 points at the top and bottom, because each tine bifurcates
 *
 * Parts of lighting (top to bottom):
 * - Root Crown: 12 roots, each with a flood pointing up
 * - Two steel rings:
 *    - Upper ring - 25' high, with 12 floods down (below platform)
 *    - Middle ring - 18' high, with 12 floods up and 12 floods down
 *  - Canopy lights
 *    -
 *  - Birds:
 *    - In branches, approximately 6' high - each bird has a LED strip (4.5 inches of 144 per meter LED strip)
 * - On the ground
 *    - Three sets of a triangle of floods (9 floods total)
 */

 public class SirsasanaModel extends LXModel {
  private static final Logger logger = Logger.getLogger(SirsasanaModel.class.getName());

  public static final float UPPER_RING_RADIUS_FT = 6f;
  public static final float LOWER_RING_RADIUS_FT = 6f;

  public static final int NUM_TINES = 6;
  public static final int CROWNS_PER_TINE = 2;  // This will be twelve.

  public static final float LOWER_RING_HEIGHT_FT = 18f;
  public static final float UPPER_RING_HEIGHT_FT = 25f;
  public static final int NUM_UPPER_RING_FLOODS = 12;
  public static final int NUM_LOWER_RING_UP_FLOODS = 12;
  public static final int NUM_LOWER_RING_DOWN_FLOODS = 12;

  public static final int TRIANGLE_BASES = 3;
  public static final float TRIANGLE_BASE_RADIUS_FT = 16f;
  public static final float LARGE_TRIANGLE_ANGLE = 90f;
  public static final float SMALL_TRIANGLE_ANGLE = 45f;
  // The flood for the large triangle will be in the center.  The floods for each of the smaller
  // triangles will be displaced +/- by this amount in polar degrees.
  public static final float BASE_ANGLE_OFFSET = 10f;

  public static final float SPIKES_TOP_SPINE_HEIGHT_FT = 28f;
  public static final float SPIKES_TOP_SPINE_RADIUS_FT = 8f;

  public static final float FLOOD_MOUNT_MARGIN_FT = 0.5f;

  public static final int NUM_CANOPY_FLOODS = 6;
  public static final float CANOPY_FLOOD_HEIGHT_FT = 7.0f;
  public static final float CANOPY_RADIUS_FT = 22.0f;

  public static final int NUM_BIRDS = 12;

  // holds flood lights
  public static List<LXPoint> upperRingFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingUpFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingDownFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> topCrownSpikeLights = new ArrayList<LXPoint>();
  public static List<LXPoint> canopyFloods = new ArrayList<LXPoint>();

  public static List<LXPoint> base1Floods;
  public static List<LXPoint> base2Floods;
  public static List<LXPoint> base3Floods;
  public static List<LXPoint> baseFloods = new ArrayList<LXPoint>();

  public static List<Bird> birds = new ArrayList<Bird>();
  public static List<LXPoint> allFloodsNoBirds = new ArrayList<LXPoint>();
  public static List<LXPoint> allPoints = new ArrayList<LXPoint>();


  static public float polarAngle(int i, int total) {
    return 360f * (float)i/(float)total;
  }

  static public float polarX(float radius, float angleDegrees) {
    return radius * (float) Math.cos(Math.toRadians(angleDegrees));
  }

  static public float polarZ(float radius, float angleDegrees) {
    return radius * (float) Math.sin(Math.toRadians(angleDegrees));
  }

  public static final float INCHES_PER_METER = 39.37f;

  // 12 birds and each bird has back to back led strip of some length. 4.5 inches of 144 per meter led strip.
  static public class Bird {
    public int id;
    public float x, y, z;
    public List<LXPoint> side1Points = new ArrayList<LXPoint>();
    public List<LXPoint> side2Points = new ArrayList<LXPoint>();
    public List<LXPoint> points = new ArrayList<LXPoint>();

    public Bird(int id, float x, float y, float z) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.z = z;

      float metersPerSide = 4.5f / INCHES_PER_METER;
      float ledSpacingMeters = 1f / 144f;
      float feetPerMeter = 3.28084f;
      float ledSpacingFeet = ledSpacingMeters / feetPerMeter;
      int ledsPerSide = (int)(metersPerSide * 144);
      for (int i = 0; i < ledsPerSide; i++) {
        LXPoint point = new LXPoint(x, y + i * ledSpacingFeet, z);
        side1Points.add(point);
      }
      for (int i = 0; i < ledsPerSide; i++) {
        LXPoint point = new LXPoint(x, y + (ledsPerSide - 1) * ledSpacingFeet - (i * ledSpacingFeet), z + 0.25f/12f);
        side2Points.add(point);
      }
      points.addAll(side1Points);
      points.addAll(side2Points);
    }
  }

  static public LXModel createModel() {
    // Create the lights from top to bottom.

    // create crown lights
    int NUM_CROWNS = CROWNS_PER_TINE * NUM_TINES;
    for (int i = 0; i <  CROWNS_PER_TINE * NUM_TINES; i++) {
      float angle = polarAngle(i,  CROWNS_PER_TINE * NUM_TINES);
      float x = polarX(SPIKES_TOP_SPINE_RADIUS_FT, angle);
      float z = polarZ(SPIKES_TOP_SPINE_RADIUS_FT, angle);
      topCrownSpikeLights.add(new LXPoint(x, SPIKES_TOP_SPINE_HEIGHT_FT, z));
    }
    allPoints.addAll(topCrownSpikeLights);

    // create upper ring lights
    for (int i = 0; i < NUM_UPPER_RING_FLOODS; i++) {
      float angle = polarAngle(i, NUM_UPPER_RING_FLOODS);
      float x = polarX(UPPER_RING_RADIUS_FT, angle);
      float z = polarZ(UPPER_RING_RADIUS_FT, angle);
      upperRingFloods.add(new LXPoint(x, UPPER_RING_HEIGHT_FT - FLOOD_MOUNT_MARGIN_FT, z));
    }
    allPoints.addAll(upperRingFloods);

    // create lower ring up-lights
    for (int i = 0; i < NUM_LOWER_RING_UP_FLOODS; i++) {
      float angle = polarAngle(i, NUM_LOWER_RING_UP_FLOODS);
      float x = polarX(LOWER_RING_RADIUS_FT, angle);
      float z = polarZ(LOWER_RING_RADIUS_FT, angle);
      lowerRingUpFloods.add(new LXPoint(x, LOWER_RING_HEIGHT_FT + FLOOD_MOUNT_MARGIN_FT, z));
    }
    allPoints.addAll(lowerRingUpFloods);

    // create lower ring down-lights
    for (int i = 0; i < NUM_LOWER_RING_DOWN_FLOODS; i++) {
      float angle = polarAngle(i, NUM_LOWER_RING_DOWN_FLOODS);
      float x = polarX(LOWER_RING_RADIUS_FT, angle);
      float z = polarZ(LOWER_RING_RADIUS_FT, angle);
      lowerRingDownFloods.add(new LXPoint(x, LOWER_RING_HEIGHT_FT - FLOOD_MOUNT_MARGIN_FT, z));
    }
    allPoints.addAll(lowerRingDownFloods);

    // canopy flood lights
    for (int i = 0; i < NUM_CANOPY_FLOODS; i++) {
      float angle = polarAngle(i, NUM_CANOPY_FLOODS);
      float x = polarX(CANOPY_RADIUS_FT, angle);
      float z = polarZ(CANOPY_RADIUS_FT, angle);
      canopyFloods.add(new LXPoint(x, CANOPY_FLOOD_HEIGHT_FT, z));
    }
    allPoints.addAll(canopyFloods);

    // create triangles at base
    for (int i = 0; i < TRIANGLE_BASES; i++) {
      List<LXPoint> baseFloodsSet = new ArrayList<LXPoint>();
      float angle = polarAngle(i, TRIANGLE_BASES);
      float x = polarX(TRIANGLE_BASE_RADIUS_FT, angle);
      float z = polarZ(TRIANGLE_BASE_RADIUS_FT, angle);
      float angle0 = angle - BASE_ANGLE_OFFSET;
      float x0 = polarX(TRIANGLE_BASE_RADIUS_FT, angle0);
      float z0 = polarZ(TRIANGLE_BASE_RADIUS_FT, angle0);
      float angle2 = angle + BASE_ANGLE_OFFSET;
      float x2 = polarX(TRIANGLE_BASE_RADIUS_FT, angle2);
      float z2 = polarZ(TRIANGLE_BASE_RADIUS_FT, angle2);
      baseFloodsSet.add(new LXPoint(x0, 0f, z0));
      baseFloodsSet.add(new LXPoint(x, 0f, z));
      baseFloodsSet.add(new LXPoint(x2, 0f, z2));
      if (i == 0)
        base1Floods = baseFloodsSet;
      else if (i == 1)
        base2Floods = baseFloodsSet;
      else if (i == 2)
        base3Floods = baseFloodsSet;
      baseFloods.addAll(baseFloodsSet);
    }
    allPoints.addAll(baseFloods);

    allFloodsNoBirds.addAll(allPoints);

    // create birds
    int birdId = 0;
    for (int i = 0; i < NUM_BIRDS/2; i++) {
      float angle = polarAngle(i, NUM_BIRDS/2);
      float x = polarX(UPPER_RING_RADIUS_FT + 1f, angle);
      float z = polarZ(UPPER_RING_RADIUS_FT + 1f, angle);
      Bird bird = new Bird(birdId++, x, UPPER_RING_HEIGHT_FT, z);
      birds.add(bird);
      allPoints.addAll(bird.points);
    }

    for (int i = 0; i < NUM_BIRDS/2; i++) {
      float angle = polarAngle(i, NUM_BIRDS/2);
      float x = polarX(LOWER_RING_RADIUS_FT + 1f, angle);
      float z = polarZ(LOWER_RING_RADIUS_FT + 1f, angle);
      Bird bird = new Bird(birdId++, x, LOWER_RING_HEIGHT_FT, z);
      birds.add(bird);
      allPoints.addAll(bird.points);
    }

    return new SirsasanaModel(allPoints);
  }


  public SirsasanaModel(List<LXPoint> points) {
    super(points);
  }

}
