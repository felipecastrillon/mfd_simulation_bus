package models;

import main.Global;

public class Daganzo implements Simulator {
  private int[] ve;
  private int[] OMEGA;
  private double deltaX;
  public void init (int MAXS[], int LENGTH, double dx) {
      OMEGA=MAXS;
      deltaX=dx;
    }

  public int updateSpeed (int speed, int gap, 
			  double prob_slowdown, double dummy_lambda, int type) {    
    
    speed =  Math.min (Global.OMEGA[type], gap);
    
    /*if (type == 1){
        int omega;
        double rand = Math.random();
        if(Math.random() <= 0.1)
            omega = OMEGA[type] + 1;
        else if ((Math.random() <= 0.2))
            omega  = OMEGA[type] - 1;
        else
            omega = OMEGA[type];
        
        speed =  Math.min (omega, gap);
    }*/
    

    return speed;
  }

  public String getName() {
    return "Daganzo/Laval";
  }
}

/*
public interface Simulator {
  void init(int MAXSPEED[], int LENGTH, double deltaX);
  int updateSpeed(int speed, int gap, double prob_slowdown, double lambda);
  String getName();
}*/