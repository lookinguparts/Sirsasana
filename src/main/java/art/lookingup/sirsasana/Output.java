package art.lookingup.sirsasana;

import art.lookingup.ui.UIPixliteConfig;
import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.output.ArtSyncDatagram;
import heronarts.lx.output.LXDatagram;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Output {
  private static final Logger logger = Logger.getLogger(Output.class.getName());

  public static int artnetPort = 6454;

  static public List<List<LXPoint>> allOutputsPoints = new ArrayList<List<LXPoint>>();

  // We keep track of these so we can restart the network.  There is no way to get a list of children from
  // LXOutputGroup which is the class for lx.engine.output.
  static public List<ArtNetDatagram> outputDatagrams = new ArrayList<ArtNetDatagram>();
  static public LXDatagram artSyncDatagram;


  public static void configureUnityArtNet(LX lx) {
    String unityIpAddress = "127.0.0.1";
    logger.log(Level.INFO, "Using ArtNet: " + unityIpAddress + ":" + artnetPort);

    List<LXPoint> points = new ArrayList<LXPoint>();
    points.addAll(SirsasanaModel.canopyFloods);
    points.addAll(SirsasanaModel.upperRingFloods);
    points.addAll(SirsasanaModel.lowerRingUpFloods);
    points.addAll(SirsasanaModel.lowerRingDownFloods);
    points.addAll(SirsasanaModel.baseFloods);

    int numUniverses = (int)Math.ceil(((double)points.size())/170.0);
    logger.info("Num universes: " + numUniverses + " for num points: " + points.size());
    List<ArtNetDatagram> datagrams = new ArrayList<ArtNetDatagram>();
    int totalPointsOutput = 0;

    for (int univNum = 0; univNum < numUniverses; univNum++) {
      int[] dmxChannelsForUniverse = new int[points.size()];
      for (int i = 0; i < 170 && totalPointsOutput < points.size(); i++) {
        LXPoint p = points.get(univNum*170 + i);
        dmxChannelsForUniverse[i] = p.index;
        totalPointsOutput++;
      }

      logger.info("Generating ArtNet datagram with points=" + dmxChannelsForUniverse.length/3);
      ArtNetDatagram artnetDatagram = new ArtNetDatagram(lx, dmxChannelsForUniverse, univNum);
      try {
        artnetDatagram.setAddress(InetAddress.getByName(unityIpAddress)).setPort(artnetPort);
      } catch (UnknownHostException uhex) {
        logger.log(Level.SEVERE, "Configuring ArtNet: " + unityIpAddress, uhex);
      }
      datagrams.add(artnetDatagram);
    }

    for (ArtNetDatagram dgram : datagrams) {
      lx.engine.addOutput(dgram);
    }
  }

  public static void configurePixliteOutput(LX lx) {
    List<ArtNetDatagram> datagrams = new ArrayList<ArtNetDatagram>();
    String artNetIpAddress = SirsasanaApp.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_IP).getString();
    int artNetIpPort = Integer.parseInt(SirsasanaApp.pixliteConfig.getStringParameter(UIPixliteConfig.PIXLITE_PORT).getString());
    logger.log(Level.INFO, "Using Pixlite ArtNet: " + artNetIpAddress + ":" + artNetIpPort);


    int universesPerOutput = 2;

    allOutputsPoints.clear();
    outputDatagrams.clear();

    for (int outputNum = 0; outputNum < 32; outputNum++) {
      List<LXPoint> outputPoints = new ArrayList<LXPoint>();
      allOutputsPoints.add(outputPoints);

      List<LXPoint> pointsWireOrder = new ArrayList<LXPoint>();
      // Output Number is 1 based in the UI.
      String mapping = SirsasanaApp.outputMap.getOutputMapping(outputNum + 1);
      logger.info("========== PIXLITE OUTPUT #" + (outputNum + 1) + "     ==============");

      logger.info("mapping=" + mapping);
      // Allow multiple components per output.  With a 1:1 mapping we are fully utilizing each long range receiver
      // so there is no room for future expansion.
      String[] components = mapping.split(",");
      for (int ci = 0; ci < components.length; ci++) {
        String ledSource = components[ci];
        if (ledSource.startsWith("crown")) {
          int crownOffset = SirsasanaApp.mapOffsets.getCrownOffset();
          logger.info("Crown Offset: " + crownOffset);
          pointsWireOrder.addAll(remapWithOffset(SirsasanaModel.topCrownSpikeLightsSorted, crownOffset));
        } else if (ledSource.startsWith("topring")) {
          int topRingOffset = SirsasanaApp.mapOffsets.getUpperRingOffset();
          logger.info("Upper Ring Offset: " + topRingOffset);
          pointsWireOrder.addAll(remapWithOffset(SirsasanaModel.upperRingFloodsSorted, topRingOffset));
        } else if (ledSource.startsWith("bringdown")) {
          int lowerRingDownOffset = SirsasanaApp.mapOffsets.getLowerRingDownOffset();
          logger.info("Lower Ring Down Offset: " + lowerRingDownOffset);
          pointsWireOrder.addAll(remapWithOffset(SirsasanaModel.lowerRingDownFloodsSorted, lowerRingDownOffset));
        } else if (ledSource.startsWith("bringup")) {
          int lowerRingUpOffset = SirsasanaApp.mapOffsets.getLowerRingUpOffset();
          logger.info("Lower Ring Up Offset: " + lowerRingUpOffset);
          pointsWireOrder.addAll(remapWithOffset(SirsasanaModel.lowerRingUpFloodsSorted, lowerRingUpOffset));
        } else if (ledSource.startsWith("gf")) {
          int groundFloodGroup = Integer.parseInt(ledSource.split("\\.")[1]) - 1;
          int groundFloodOffset = SirsasanaApp.mapOffsets.getGroundOffset();
          logger.info("Ground Flood Offset: " + groundFloodOffset);
          // To allow for rotation of the ground floods in case of a miswiring, we will just adjust the entire
          // set of 3 ground floods since it shouldn't be the case that everything is off by just a single
          // ground flood.  So just remap the group.
          groundFloodGroup = (groundFloodGroup + groundFloodOffset) % 3;
          pointsWireOrder.addAll(SirsasanaModel.groundFloodGroups.get(groundFloodGroup));
        } else if (ledSource.startsWith("cg")) {
          int canopyGroup = Integer.parseInt(ledSource.split("\\.")[1]) - 1;
          int canopyGroupOffset = SirsasanaApp.mapOffsets.getCanopyOffset();
          logger.info("Canopy Group Offset: " + canopyGroupOffset);
          // Same as with ground floods, individual canopy lights should not be wired with the wrong starting location
          // but all the sets-of-2 lights might need to be shifted.
          canopyGroup = (canopyGroup + canopyGroupOffset) % 6;  // 6 sets of 2-per-group canopy lights.
          pointsWireOrder.addAll(SirsasanaModel.canopyFloodGroups.get(canopyGroup));
        } else if (ledSource.startsWith("b")) {
          int birdNum = Integer.parseInt(ledSource.split("\\.")[1]) - 1;
          int birdOffset = SirsasanaApp.mapOffsets.getBirdOffset();
          logger.info("Bird offset: " + birdOffset);
          // Also allow the bird #'s to be rotated for mapping purposes but this probably won't be necessary.
          birdNum = (birdNum + birdOffset) % 12;
          pointsWireOrder.addAll(SirsasanaModel.birds.get(birdNum).points);
        }
      }

      outputPoints.addAll(pointsWireOrder);

      int numUniversesThisWire = (int) Math.ceil((float) pointsWireOrder.size() / 170f);
      int univStartNum = outputNum * universesPerOutput;
      int lastUniverseCount = pointsWireOrder.size() - 170 * (numUniversesThisWire - 1);
      int maxLedsPerUniverse = (pointsWireOrder.size()>170)?170:pointsWireOrder.size();
      int[] thisUniverseIndices = new int[maxLedsPerUniverse];
      int curIndex = 0;
      int curUnivOffset = 0;
      for (LXPoint pt : pointsWireOrder) {
        thisUniverseIndices[curIndex] = pt.index;
        curIndex++;
        if (curIndex == 170 || (curUnivOffset == numUniversesThisWire - 1 && curIndex == lastUniverseCount)) {
          logger.log(Level.INFO, "Adding datagram: for: " + mapping + " PixLite universe=" + (univStartNum + curUnivOffset + 1) + " ArtNet universe=" + (univStartNum + curUnivOffset) + " points=" + curIndex);
          ArtNetDatagram datagram = new ArtNetDatagram(lx, thisUniverseIndices, univStartNum + curUnivOffset);
          try {
            datagram.setAddress(InetAddress.getByName(artNetIpAddress)).setPort(artNetIpPort);
          } catch (UnknownHostException uhex) {
            logger.log(Level.SEVERE, "Configuring ArtNet: " + artNetIpAddress + ":" + artNetIpPort, uhex);
          }
          datagrams.add(datagram);
          curUnivOffset++;
          curIndex = 0;
          if (curUnivOffset == numUniversesThisWire - 1) {
            thisUniverseIndices = new int[lastUniverseCount];
          } else {
            thisUniverseIndices = new int[maxLedsPerUniverse];
          }
        }
      }
    }
    for (ArtNetDatagram dgram : datagrams) {
      lx.engine.addOutput(dgram);
      outputDatagrams.add(dgram);
    }

    try {
      artSyncDatagram = new ArtSyncDatagram(lx).setAddress(InetAddress.getByName(artNetIpAddress)).setPort(artNetIpPort);
      lx.engine.addOutput(artSyncDatagram);
    } catch (UnknownHostException unhex) {
      logger.info("Uknown host exception for Pixlite IP: " + artNetIpAddress + " msg: " + unhex.getMessage());
    }
  }

  /**
   * Since this is a rotationally symmetric installation, provide a utility function for mapping that allows for
   * the starting point for the data wiring to be different than expected.  Ideally all wiring would start at
   * polar angle >=0 but this allows for a remapping should they not be installed correctly.
   * @param points
   * @param offset
   * @return
   */
  static public List<LXPoint> remapWithOffset(List<LXPoint> points, int offset) {
    List<LXPoint> pointsWireOrder = new ArrayList<LXPoint>();
    if (offset > 0) {
      pointsWireOrder.addAll(points.subList(offset, points.size()));
      pointsWireOrder.addAll(points.subList(0, offset));
    } else {
      pointsWireOrder.addAll(points);
    }
    return pointsWireOrder;
  }

  static public void restartOutput(LX lx) {
    boolean originalEnabled = lx.engine.output.enabled.getValueb();
    lx.engine.output.enabled.setValue(false);
    for (ArtNetDatagram dgram : outputDatagrams) {
      lx.engine.output.removeChild(dgram);
    }
    lx.engine.output.removeChild(artSyncDatagram);
    configurePixliteOutput(lx);
    lx.engine.output.enabled.setValue(originalEnabled);
  }
}
