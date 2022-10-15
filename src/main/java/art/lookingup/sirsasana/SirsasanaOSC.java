package art.lookingup.sirsasana;

import heronarts.lx.LX;
import heronarts.lx.osc.LXOscEngine;
import heronarts.lx.osc.LXOscListener;
import heronarts.lx.osc.OscMessage;

import java.util.List;
import java.util.logging.Logger;

public class SirsasanaOSC implements LXOscListener {
  private static final Logger logger = Logger.getLogger(SirsasanaModel.class.getName());
  public LX lx;
  public static int OSC_PORT = 7979;

  public SirsasanaOSC(LX lx) {
    this.lx = lx;

    try {
      // Register for custom OSC messages on a dedicated port
      LXOscEngine.Receiver r = lx.engine.osc.receiver(OSC_PORT).addListener(this);
      logger.info("OSC Receiver enabled.");
    } catch (java.net.SocketException sx) {
      throw new RuntimeException(sx);
    }
  }

  /**
   * Handle an OSC message from a client.  Root path of address space is /rainbow.
   *
   * registerclient: String argument of form host:port.  RainbowStudio will start sending control
   * data to the specified destination.  Used for phone/tablet control.
   *
   * @param message OscMessage from a client.
   */
  public void oscMessage(OscMessage message) {

    try {
      String addressPattern = message.getAddressPattern().getValue();
      String[] path = addressPattern.split("/");
      //logger.info("Received OSC message at path: " + addressPattern + " = " + message.getString());

      if ("sirsasana".equals(path[1])) {
        if (path.length > 2) {

          if ("birdvol".equals(path[2])) {
            int ctrlSigNum = message.getInt(0);
            float vol = message.getFloat(1);
            if (ctrlSigNum < 0 || ctrlSigNum >= SirsasanaModel.birds.size()) {
              logger.info("Invalid bird number: " + message.getString());
              return;
            }
            if (SirsasanaApp.ctrlSigMap == null) {
              Bird bird = SirsasanaModel.birds.get(ctrlSigNum);
              //logger.info("birdvol: bird=" + birdNum + " vol=" + vol);
              bird.volume = vol;
            } else {
              // Get the list of birds to map this control signal to.
              List<Integer> birdIds = SirsasanaApp.ctrlSigMap.ctrlMap.get(ctrlSigNum);
              for (int birdId : birdIds) {
                // In the UI control signal mapping, the bird ids are 1 based so remap to zero based.
                Bird bird = SirsasanaModel.birds.get(birdId - 1);
                bird.volume = vol;
              }
            }
          }
        }
      }
    } catch (Exception ex) {
      logger.severe("Error handling OSC message: " + ex.getMessage());
    }
  }

}
