package art.lookingup.sirsasana;

import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

// 12 birds and each bird has back to back led strip of some length. 4.5 inches of 144 per meter led strip.
public class Bird {
  public int id;
  public int midiChannel;
  public float x, y, z;
  public List<LXPoint> side1Points = new ArrayList<LXPoint>();
  public List<LXPoint> side2Points = new ArrayList<LXPoint>();
  public List<LXPoint> points = new ArrayList<LXPoint>();

  public Bird(int id, float x, float y, float z) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.z = z;
    this.midiChannel = id + 1;

    float metersPerSide = 4.5f / SirsasanaModel.INCHES_PER_METER;
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

  /**
   * If we give a bird a new channel, swap the old bird with our current channel.  This is just a precaution so
   * that we don't disappear birds from the midiToBird mapping.
   * @param newChannel
   */
  public void changeMidiChannel(int newChannel) {
    Bird otherBird = SirsasanaModel.midiToBird.get(newChannel);
    if (otherBird != null) {
      otherBird.midiChannel = this.midiChannel;
      SirsasanaModel.midiToBird.put(otherBird.midiChannel, otherBird);
    } else {
      // There is no bird at our new channel to swap into our old position, so just remove our old mapping.
      SirsasanaModel.midiToBird.remove(this.midiChannel);
    }
    this.midiChannel = newChannel;
    SirsasanaModel.midiToBird.put(this.midiChannel, this);
  }
}
