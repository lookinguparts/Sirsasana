package art.lookingup.sirsasana;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
 * - Root Crown: 12 roots, each with 3 LED pucks mounted somehow.  36 lights.
 * - Two steel rings:
 *    - Upper ring - 25' high, with 6 floods down (below platform)
 *    - Middle ring - 18' high, with 6 floods up and 6 floods down
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
  public static final int CROWNS_PER_TINE = 2;

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
  public static final float CANOPY_FLOOD_HEIGHT_FT = 8.0f;
  public static final float CANOPY_RADIUS_FT = 22.0f;

  // birds are stored in pairs around the circle - let's assume they're separated by about 5 degrees
  public static final int NUM_BIRDS = 12;
  public static final int NUM_BIRD_PAIRS = NUM_BIRDS / 2;
  public static final int BIRD_PAIR_OFFSET_DEGREES = 5;
  public static final float BIRD_HEIGHT_FT = 6.0f;
  public static final float BIRD_RADIUS_FT = 16.0f;

  // holds layers of lights
  public static List<LXPoint> upperRingFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> upperRingFloodsSorted = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingUpFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingUpFloodsSorted = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingDownFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> lowerRingDownFloodsSorted = new ArrayList<LXPoint>();
  public static List<LXPoint> topCrownSpikeLights = new ArrayList<LXPoint>();
  public static List<LXPoint> topCrownSpikeLightsSorted = new ArrayList<LXPoint>();
  public static List<LXPoint> canopyFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> canopyFloodsSorted = new ArrayList<LXPoint>();
  public static List<List<LXPoint>> canopyFloodGroups = new ArrayList<List<LXPoint>>();

  public static List<LXPoint> base1Floods;
  public static List<LXPoint> base2Floods;
  public static List<LXPoint> base3Floods;
  public static List<LXPoint> baseFloods = new ArrayList<LXPoint>();
  public static List<LXPoint> baseFloodsSorted = new ArrayList<LXPoint>();
  // After sorting, we will divide up all the ground floods into 3 groups of three.  We do this to provide
  // some flexibility in the output wiring mapping.
  public static List<List<LXPoint>> groundFloodGroups = new ArrayList<List<LXPoint>>();

  public static List<Bird> birds = new ArrayList<Bird>();
  public static List<LXPoint> allBirdPoints = new ArrayList<LXPoint>();
  public static Map<Integer, Bird> midiToBird = new HashMap<Integer, Bird>();

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

  /*
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
    // they will live in small branches in pairs  - for now, we'll put them in pairs in a small
    // ring around the center, offset slightly in degrees
    int birdId = 0;
    for (int i = 0; i < NUM_BIRD_PAIRS; i++) {
      float angle = polarAngle(i, NUM_BIRD_PAIRS);

      // create first bird in pair
      float x1 = polarX(BIRD_RADIUS_FT, angle);
      float z1 = polarZ(BIRD_RADIUS_FT, angle);
      Bird bird1 = new Bird(birdId++, x1, BIRD_HEIGHT_FT, z1);
      birds.add(bird1);
      allPoints.addAll(bird1.points);
      allBirdPoints.addAll(bird1.points);

      // create second bird in pair
      float x2 = polarX(BIRD_RADIUS_FT, angle + BIRD_PAIR_OFFSET_DEGREES);
      float z2 = polarZ(BIRD_RADIUS_FT, angle + BIRD_PAIR_OFFSET_DEGREES);
      Bird bird2 = new Bird(birdId++, x2, BIRD_HEIGHT_FT, z2);
      birds.add(bird2);
      allPoints.addAll(bird2.points);
      allBirdPoints.addAll(bird2.points);
    }

    return new SirsasanaModel(allPoints);
  }
*/

  /**
   * Creates the lighting model from positions exported from Rhino.
   * @return
   */
  static public LXModel createModelFromPositions() {
    final String crownFile = "crownpositions.txt";

    LXPointAngleComparator comparator = new LXPointAngleComparator();

    topCrownSpikeLights = csvToLXPoints("crownlights.txt", 1f/12f);
    topCrownSpikeLightsSorted.addAll(topCrownSpikeLights);
    Collections.sort(topCrownSpikeLightsSorted, comparator);
    allPoints.addAll(topCrownSpikeLights);

    upperRingFloods = csvToLXPoints("upperlights.txt", 1f/12f);
    upperRingFloodsSorted.addAll(upperRingFloods);
    Collections.sort(upperRingFloodsSorted, comparator);
    allPoints.addAll(upperRingFloods);

    List<LXPoint> lowerFloods = csvToLXPoints("lowerlights.txt", 1f/12f);
    lowerRingUpFloods.addAll(lowerFloods.subList(0, lowerFloods.size()/2));
    lowerRingUpFloodsSorted.addAll(lowerRingUpFloods);
    Collections.sort(lowerRingUpFloods, comparator);
    allPoints.addAll(lowerRingUpFloods);
    lowerRingDownFloods.addAll(lowerFloods.subList(lowerFloods.size()/2, lowerFloods.size()));
    lowerRingDownFloodsSorted.addAll(lowerRingDownFloods);
    Collections.sort(lowerRingDownFloods, comparator);
    allPoints.addAll(lowerRingDownFloods);

    canopyFloods = csvToLXPoints("canopylights.txt", 1f/12f);
    canopyFloodsSorted.addAll(canopyFloods);
    Collections.sort(canopyFloodsSorted, comparator);
    for (int i = 0; i < 6; i++) {
      List<LXPoint> cfGroup = new ArrayList<LXPoint>();
      cfGroup.add(canopyFloodsSorted.get(i * 2));
      cfGroup.add(canopyFloodsSorted.get(i * 2 + 1));
      canopyFloodGroups.add(cfGroup);
    }
    allPoints.addAll(canopyFloods);

    // Preserve the original order so that the Unity output matches up
    baseFloods = csvToLXPoints("groundlights.txt", 1f/12f);
    allPoints.addAll(baseFloods);
    baseFloodsSorted.addAll(baseFloods);
    // Sort base floods by polar angle.
    Collections.sort(baseFloodsSorted, new LXPointAngleComparator());
    // Put into groups of three
    for (int i = 0; i < 3; i++) {
      List<LXPoint> gfGroup = new ArrayList<LXPoint>();
      gfGroup.add(baseFloodsSorted.get(i * 3));
      gfGroup.add(baseFloodsSorted.get(i * 3 + 1));
      gfGroup.add(baseFloodsSorted.get(i * 3 + 2));
      groundFloodGroups.add(gfGroup);
    }

    allFloodsNoBirds.addAll(allPoints);

    List<Point3D> birdPositions = csvToPoint3Ds("birdlights.txt", 1f/12f);
    int birdId = 0;
    for (Point3D birdPos : birdPositions) {
      Bird bird = new Bird(birdId++, birdPos.x, birdPos.y, birdPos.z);
      birds.add(bird);
      allBirdPoints.addAll(bird.points);
      // First bird is Midi Channel 1 by default.
      // TODO(tracy): Allow this to be remapped in the UI.
      midiToBird.put(bird.midiChannel, bird);
    }
    allPoints.addAll(allBirdPoints);

    return new SirsasanaModel(allPoints);
  }


  public SirsasanaModel(List<LXPoint> points) {
    super(points);
  }


  static public List<String> readFileToStrings(String filename) {
    List<String> lines = new ArrayList<String>();
    try {
      File file = new File(filename);
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException ioex) {
    }
    return lines;
  }

  static public class Point3D {
    float x, y, z;
    public Point3D(float x, float y, float z) {
      this.x = x; this.y = y; this.z = z;
    }
  }

  static public List<Point3D> csvToPoint3Ds(String filename, float unitConversion) {
    List<Point3D> point3Ds = new ArrayList<Point3D>();
    List<String> lines = readFileToStrings(filename);
    for (String line : lines) {
      if (line.contains(",")) {
        String[] ledPosXYZ = line.split(",");
        float x = Float.parseFloat(ledPosXYZ[0]);
        float y = Float.parseFloat(ledPosXYZ[2]);
        float z = Float.parseFloat(ledPosXYZ[1]);
        point3Ds.add(new Point3D(x * unitConversion, y * unitConversion, z * unitConversion));
      }
    }
    return point3Ds;
  }

  static public List<LXPoint> csvToLXPoints(String filename, float unitConversion) {
    List<LXPoint> points = new ArrayList<LXPoint>();
    logger.info("Loading Points: " + filename);
    List<String> lines = readFileToStrings(filename);
    for (String line : lines) {
      if (line.contains(",")) {
        String[] ledPosXYZ = line.split(",");
        float x = Float.parseFloat(ledPosXYZ[0]);
        float y = Float.parseFloat(ledPosXYZ[2]);
        float z = Float.parseFloat(ledPosXYZ[1]);
        points.add(new LXPoint(x * unitConversion, y * unitConversion,  z * unitConversion));
      }
    }
    logger.info("Num points added: " + points.size());
    return points;
  }

  static public class LXPointAngleComparator implements Comparator<LXPoint> {
    @Override
    public int compare(LXPoint a, LXPoint b) {
      return polar(a) < polar(b)? -1 : polar(a) == polar(b) ? 0: 1;
    }

    public double polar(LXPoint a) {
      double angle = Math.atan2(a.z, a.x);
      // Fix -PI to PI range to be 0 to 2 PI
      if (angle > 0) return angle;
      else return Math.PI * 2f + angle;
    }
  }
}
