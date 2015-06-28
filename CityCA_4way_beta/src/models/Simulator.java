package models;

public interface Simulator {
  void init(int MAXSPEED[], int LENGTH, double deltaX);
  int updateSpeed(int speed, int gap, double prob_slowdown, double lambda,int type);
  String getName();
}
