package main;
import java.awt.*;
import java.util.ArrayList;

public class Global {
    //get global variables
    public static final int WE=1,EW=2,NS=3,SN=4;
  // model Parameters
    public static final int car_lg = 1;
    public static final int bus_lg = 1;
    public static int[] OMEGA = {4,2,2};
    public static final int car_spd = OMEGA[0];
    public static int bus_spd = OMEGA[1];
    public static final int cw_spd = car_lg;
    public static final int deltaX = 7; //m
    public static int blocks = CityApplet.blocks;
    public static final int lanes = CityApplet.lanes;
    public static int length = CityApplet.length;
    public static final int repeatnet=1;
    public static double green = Node.Ncycle*Node.NgocEW;
    public static int cycle = Node.Ncycle;
    public static int offset = 2;//Node.Noffset0;
    public static final double slowdown = 0.01;
    public static final double density  = 0.2;
    public static final double lambda   = 0.77;
    public static final double maxtime   = 500;
    public static double delta   = 0;
    public static final int maxlength   = Global.blocks*Global.repeatnet*Global.length;
            //(int)Math.sqrt((double)Global.length)*700;//(int)(maxtime*3.5);
    public static double tvheadway   = 0;
    public static int dwell   = 20;
    public static double pstop   = 1;
    public static final ArrayList<Double> rgprop = new  ArrayList<Double>();
    public static final ArrayList<Integer> cycles = new  ArrayList<Integer>();
    public static final ArrayList<Integer> lengths = new  ArrayList<Integer>();
    public static DiagramPane diag =new DiagramPane(0);     
    public static boolean nonrandomstop = false;
    
    public static Color[] col = {Color.blue,Color.red,Color.CYAN,Color.orange,Color.MAGENTA,
        Color.GREEN,Color.yellow,Color.pink};

    public static final  String nodeName(int i, int j) {
        return i + " - " + j;
    }

    public static final  String nodeName(double i, double j) {
        return i + " - " + j;
    }
    
    public static Color CarColor(int v) {
        Color c=Color.black;
        switch(v){
            case 0:c= new Color(0,180,0); break;
            case 1:c=new Color(0,185,0); break;
            case 2:c=new Color(0,190,0); break;
            case 3:c=new Color(0,195,0); break;
            case 4:c=new Color(0,200,0); break;
            case 5:c=Color.PINK; break;
        }
        return c;
    }

    public static Color rndColor() {
        Color c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        return c;
    }

    public static void drawArrow(Graphics2D g2d, int xCenter, int yCenter, int x, int y, float stroke) {
      double aDir=Math.atan2(xCenter-x,yCenter-y);
      g2d.drawLine(x,y,xCenter,yCenter);
      g2d.setStroke(new BasicStroke(1f));					// make the arrow head solid even if dash pattern has been specified
      Polygon tmpPoly=new Polygon();
      int i1=12+(int)(stroke*2);
      int i2=6+(int)stroke;							// make the arrow head the same size regardless of the length length
      tmpPoly.addPoint(x,y);							// arrow tip
      tmpPoly.addPoint(x+xCor(i1,aDir+.5),y+yCor(i1,aDir+.5));
      tmpPoly.addPoint(x+xCor(i2,aDir),y+yCor(i2,aDir));
      tmpPoly.addPoint(x+xCor(i1,aDir-.5),y+yCor(i1,aDir-.5));
      tmpPoly.addPoint(x,y);							// arrow tip
      g2d.drawPolygon(tmpPoly);
      g2d.fillPolygon(tmpPoly);						// remove this line to leave arrow head unpainted
    }
    private static int yCor(int len, double dir) {return (int)(len * Math.cos(dir));}
    private static int xCor(int len, double dir) {return (int)(len * Math.sin(dir));}

    public static String TimeToString(int ttt) {
        String tt="";
        int h, m, s;
        h = ttt/(3600);
        m = (ttt%(3600))/60;
        s = ttt-m*60-h*3600;
        return h+":"+m+":"+s;
    }
}
