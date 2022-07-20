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
 *
 * Middle ring has 6 tine connections. 12 tines at bottom because they bifurcate.
 * Each of the 6 tines is connected to 4 other tines.
 *
 * Two rings. Upper deck at 25'. Lower rings at 18'.
 *
 * Top ring 12 floods down.
 *
 * Middle ring 12 floods up, 12 floods down.
 *
 * On the ground. Two small triangles and then one larger superset triangle. 3 sets of those. Each triangle
 * gets a flood. So 3 floods per triangle set and 9 floods total.
 *
 * At the top, each end root will get an acrylic spike that lights up. 12 of them. Currently floods pointing up.
 *
 * 12 birds and each bird has back to back led strip of some length. 4.5 inches of 144 per meter led strip.
 */
public class SirsasanaModel extends LXModel {
  private static final Logger logger = Logger.getLogger(SirsasanaModel.class.getName());

  public static final float UPPER_RING_RADIUS = 6f;
  public static final float LOWER_RING_RADIUS = 6f;
  public static final int MAIN_TINES = 6;
  public static final int SPLIT_TINES = 12;
  public static final float LOWER_RING_HEIGHT = 18f;
  public static final float UPPER_RING_HEIGHT = 25f;
  public static final int UPPER_RING_FLOODS = 12;
  public static final int LOWER_RING_UP_FLOODS = 12;
  public static final int LOWER_RING_DOWN_FLOODS = 12;

  public static final int TRIANGLE_BASES = 3;
  public static final float TRIANGLE_BASE_RADIUS = 16f;
  public static final float LARGE_TRIANGLE_ANGLE = 90f;
  public static final float SMALL_TRIANGLE_ANGLE = 45f;
  // The flood for the large triangle will be in the center.  The floods for each of the smaller
  // triangles will be displaced +/- by this amount in polar degrees.
  public static final float BASE_ANGLE_OFFSET = 10f;

  public static final int SPIKES_PER_TOP_SPINE = 1;  // This will be twelve.
  public static final float SPIKES_TOP_SPINE_HEIGHT = 28f;
  public static final float SPIKES_TOP_SPINE_RADIUS = 8f;

  public static final float FLOOD_MOUNT_MARGIN = 0.5f;

  public static List<LXPoint> upperRingFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingUpFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingDownFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> topSpineSpikeLights = new ArrayList<LXPoint>();

  public static List<LXPoint> base1Floods;
  public static List<LXPoint> base2Floods;
  public static List<LXPoint> base3Floods;
  public static List<LXPoint> baseFloods = new ArrayList<LXPoint>();

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

  static public LXModel createModel() {
    // Create the lights from top to bottom.
    for (int i = 0; i < SPIKES_PER_TOP_SPINE * SPLIT_TINES; i++) {
      float angle = polarAngle(i, SPIKES_PER_TOP_SPINE * SPLIT_TINES);
      float x = polarX(SPIKES_TOP_SPINE_RADIUS, angle);
      float z = polarZ(SPIKES_TOP_SPINE_RADIUS, angle);
      topSpineSpikeLights.add(new LXPoint(x, SPIKES_TOP_SPINE_HEIGHT, z));
    }
    allPoints.addAll(topSpineSpikeLights);

    for (int i = 0; i < UPPER_RING_FLOODS; i++) {
      float angle = polarAngle(i, UPPER_RING_FLOODS);
      float x = polarX(UPPER_RING_RADIUS, angle);
      float z = polarZ(UPPER_RING_RADIUS, angle);
      upperRingFloods.add(new LXPoint(x, UPPER_RING_HEIGHT - FLOOD_MOUNT_MARGIN, z));
    }
    allPoints.addAll(upperRingFloods);

    for (int i = 0; i < LOWER_RING_UP_FLOODS; i++) {
      float angle = polarAngle(i, LOWER_RING_UP_FLOODS);
      float x = polarX(LOWER_RING_RADIUS, angle);
      float z = polarZ(LOWER_RING_RADIUS, angle);
      lowerRingUpFloods.add(new LXPoint(x, LOWER_RING_HEIGHT + FLOOD_MOUNT_MARGIN, z));
    }
    allPoints.addAll(lowerRingUpFloods);

    for (int i = 0; i < LOWER_RING_DOWN_FLOODS; i++) {
      float angle = polarAngle(i, LOWER_RING_DOWN_FLOODS);
      float x = polarX(LOWER_RING_RADIUS, angle);
      float z = polarZ(LOWER_RING_RADIUS, angle);
      lowerRingDownFloods.add(new LXPoint(x, LOWER_RING_HEIGHT - FLOOD_MOUNT_MARGIN, z));
    }
    allPoints.addAll(lowerRingDownFloods);

    for (int i = 0; i < TRIANGLE_BASES; i++) {
      List<LXPoint> baseFloodsSet = new ArrayList<LXPoint>();
      float angle = polarAngle(i, TRIANGLE_BASES);
      float x = polarX(TRIANGLE_BASE_RADIUS, angle);
      float z = polarZ(TRIANGLE_BASE_RADIUS, angle);
      float angle0 = angle - BASE_ANGLE_OFFSET;
      float x0 = polarX(TRIANGLE_BASE_RADIUS, angle0);
      float z0 = polarZ(TRIANGLE_BASE_RADIUS, angle0);
      float angle2 = angle + BASE_ANGLE_OFFSET;
      float x2 = polarX(TRIANGLE_BASE_RADIUS, angle2);
      float z2 = polarZ(TRIANGLE_BASE_RADIUS, angle2);
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

    return new SirsasanaModel(allPoints);
  }


  public SirsasanaModel(List<LXPoint> points) {
    super(points);
  }

}
