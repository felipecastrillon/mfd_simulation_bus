/*package models;

public class Helbing implements Simulator {
  private int[] ve;
  private double deltaX;

  public void init (int MAXSPEED, int LENGTH, double deltaX) {
    double s, rh, U;

    this.deltaX = deltaX;
    ve = new int[LENGTH];
    for (int gap = 0; gap < LENGTH; gap++) {
      s = (gap+1) * deltaX;
      rh = 1000.0 / s;

      U = MAXSPEED/110.0 * (28.125*(1.0-rh/160.0) + 
			    82.875*Math.exp (-Math.pow (rh/36.0,2.8)) - 
			    1.0*Math.exp (-rh*28.125/160.0));

      ve[gap] = (int) (U+0.5);
      if (ve[gap] < 0) ve[gap] = 0;

      // System.out.println (gap + "\t" + ve[gap]);
    }
  }

  // update speed according to Dirk Helbing's rules
  public int updateSpeed (int speed, int gap, 
			  double prob_slowdown, double lambda) {
    speed += Math.floor(lambda*(ve[gap]-speed));

    // slowdown with probability prob_slowdown
    if ((speed > 0) && (Math.random() < prob_slowdown)) speed--;

    return speed;
  }

  public String getName() {
    return "Helbing";
  }
}
*/