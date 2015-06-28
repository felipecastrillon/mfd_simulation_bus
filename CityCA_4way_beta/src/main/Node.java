package main;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.KShortestPaths;

public class Node {
    public static int Ncycle=60, Noffset0 = 0, Noffset1 = 0, rred=0,cycle=60; 
    public int offset = 0;
    public int i, j;
    public int block;
    public double t,b;
    public static double NgocEW=.5;
    public double rndCycle=0;
    public  int[] tIniGreen={0,0}, tEndGreen={0,0};
    public String name;
    private int time;
    private int endcyclet=0;
    private double redphase,grphase; //time when next phase ends
    private double[] meanphase = {Global.green,Global.cycle-Global.green};
    private double cov=0.3; //cov for stochastic timing
    BellmanFordShortestPath sp;
    KShortestPaths ksp;
    Map<Node, List<Double>> hashoflistsprobs = new HashMap<Node, List<Double>>();
    Map<Node, List<GraphPath>> hashoflistspaths = new HashMap<Node, List<GraphPath>>();
    Map<Node,List<GraphPath>> odtopaths= new HashMap<Node,List<GraphPath>>();
    
    public Node() {
    }

    public Node(int ii, int jj) {
        i = ii;
        j = jj;
        name = Global.nodeName(i, j);
        setSignalTiming(0);
    }
    
    public Node(double tt, double bb) {
        if (tt>0 && bb>0 && bb%12>0){
            //System.out.print("here\n");
        }
        b = bb;
        t = tt;
        name = Global.nodeName(t, b);        
        setSignalTiming(0);
    }
    
    public void setSignalTiming(int tt) {
        offset=Noffset0*i+Noffset1*j;
        time=CityApplet.t;
        if(time>=endcyclet){
            setMeanPhase(Global.green,Global.cycle-Global.green);
            redphase = getGaussian(meanphase[1],Global.delta);
            grphase = getGaussian(meanphase[0],Global.delta);
            cycle=(int)((1+rndCycle)*Ncycle);
            int gEW=(int)(cycle*NgocEW);
            tIniGreen[0] = time;
            tEndGreen[0] = time+(int)grphase;
            tIniGreen[1] = time+(int)grphase;
            tEndGreen[1] = time+(int)(grphase+redphase);
            endcyclet = time+(int)(grphase+redphase);
        }
    }
        
    public void resetTiming(){
        offset=Noffset0*(Global.blocks-i)+Noffset1*(Global.blocks-j);
        tIniGreen[0] = 0;
        tEndGreen[0] = offset;
        tIniGreen[1] = offset;
        tEndGreen[1] = offset;
        endcyclet = offset;
    }
    
    public void setMeanPhase(double gr, double rd){
        meanphase[0] = gr;
        meanphase[1] = rd;
    }
    
    private double getGaussian(double aMean, double aCov){
        Random fRandom = new Random();
        return aMean + fRandom.nextGaussian()*aMean*aCov;
    }
}
