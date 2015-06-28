package models;
/*
public class BarlovicSchreckenberg implements Simulator {
  private int[] ve;

  public void init (int MAXSPEED, int LENGTH, double deltaX) {
    // create v[gap] relation to be able to deal with flexible cell lengths 
    ve = new int[LENGTH];
    for (int gap = 0; gap < LENGTH; gap++) {
      ve[gap] = Math.min (MAXSPEED, (int)(gap+1-(7.5/deltaX)));
      if (ve[gap] < 0) ve[gap] = 0;
    }
  }

  // update speed according to Nagel-Schreckenberg rules 
  // (slightly modified to allow flexible cell lengths)
  public int updateSpeed (int speed, int gap, 
			  double prob_slowdown, double lambda) {
    double h;
    
    // Barlovic modification
    if (speed >= 1) prob_slowdown=0.01;

    h = (lambda*(ve[gap]-speed));
    if (h > 0) h = 1;
    speed += (int)h;

    // slowdown with probability prob_slowdown
    if ((speed > 0) && (Math.random() < prob_slowdown)) speed--;
    
    return speed;
  }

  public String getName() {
    return "Barlovic/Schreck.";
  }
}
*/