package art.lookingup;

/**
 * Easing functions.
 * t
 * sin((t * PI) / 2)
 * 1 - (1 - t) * (1 - t)
 * 1 - pow(1 - t, 3)
 * 1 - pow(1 - t, 4)
 * 1 - pow(1 - t, 5)
 * .5 * sin((t * 4 * PI) / 2) + .5
 */

public class EaseUtil {

  static public final int MAX_EASE = 6;

  static public float ease(float t, int which) {
    switch (which) {
      case 0:
        return ease0(t);
      case 1:
        return ease1(t);
      case 2:
        return ease2(t);
      case 3:
        return ease3(t);
      case 4:
        return ease4(t);
      case 5:
        return ease5(t);
      case 6:
        return ease6(t);
    }
    return t;
  }

  static public float ease0(float t) {
    return t;
  }

  static public float ease1(float t) {
    return (float)Math.sin(t * Math.PI / 2);
  }

  static public float ease2(float t) {
    return 1.0f - (1.0f - t) * (1.0f - t);
  }

  static public float ease3(float t) {
    return 1.0f - (float)Math.pow(1.0 - t, 3);
  }

  static public float ease4(float t) {
    return 1.0f - (float)Math.pow(1.0 - t, 4);
  }

  static public float ease5(float t) {
    return 1.0f - (float)Math.pow(1.0 - t, 5);
  }

  static public float ease6(float t) { return ease6(t, 1f); }

  static public float ease6(float t, float freq) { return 0.5f + 0.5f * (float)Math.sin(freq * t * Math.PI * 4); }
}
