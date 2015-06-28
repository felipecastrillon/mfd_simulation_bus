package models;
/*
public class KernerKonhaeuser implements Simulator {
  private int[] ve;
  private double deltaX;

  public void init (int MAXSPEED, int LENGTH, double deltaX) {
    double s, rh, U;
    final double rhohat = 175.0;
    final double rhoi = 38.5;
    final double b = 0.05;
    final double d = 1.0/(1.0+Math.exp((1.0-rhoi/rhohat)/b));
    final double vaumax = 100.8;

    this.deltaX = deltaX;
    ve = new int[LENGTH];
    for (int gap = 0; gap < LENGTH; gap++) {
      s = (gap+1) * deltaX;
      rh = 1000.0 / s;

      U = (1.05*vaumax*(1.0/(1.0+Math.exp((rh-rhoi)/(b*rhohat)))-d))/
	  (3.6*deltaX);

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
    return "Kerner/Konh.";
  }
}
*/