package art.lookingup.sirsasana;

/**
 * Class for keeping track of frog playing.  We will only play one frog at a time and on a random speaker.
 */
public class Frog {
  // The bird scheduler will pick a random future time after the minimum wait time and assign this value.
  // The bird will start playing when System.currentTimeMillis() > startPlayingAt.
  static public long lastSinging = 0;
  static public long startPlayingAt = 0;
  static public boolean waitingToPlay = false;
  static public boolean playing = false;
}
