package models;
/*
public class BandoSugiyama implements Simulator {
  private int[] ve;
  private double deltaX;

  private double tanh (double x) {
    double exp = Math.exp(-2*x);
    return (1-exp)/(1+exp);
  }

  public void init (int MAXSPEED, int LENGTH, double deltaX) {
    double s, U;
    final double s0 = 25.0;
    final double a0 = 0.086;
    final double smin = 5.0;

    this.deltaX = deltaX;
    ve = new int[LENGTH];
    for (int gap = 0; gap < LENGTH; gap++) {
      s = (gap+1) * deltaX;

      U = MAXSPEED/108.0 * (16.8 * 3.6 * 
			    (tanh(a0*(s-s0)) - tanh(a0*(smin-s0))));

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
    return "Bando/Sugiyama";
  }
}
*/