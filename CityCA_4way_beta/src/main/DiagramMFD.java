package main;

import java.awt.Color;
import java.io.PrintWriter;

public class DiagramMFD extends Diagram{
    int drawx,drawy;
    int ind = 0;
    public static PrintWriter outfile;
    
    DiagramMFD(String unitlabel,double maxx,double maxy){
        super(unitlabel, maxx, maxy);
    }
    public void paintupdate(double x, double y){
        int[] drawpoint=plots.pointbrush(x, y);
        bgmfd.fillRect(drawpoint[0]-2, drawpoint[1]-2, 4, 4);
        repaint();
    }
    public void paintupdate(double x, double y, Color c){
        bgmfd.setColor(c);
        int[] drawpoint=plots.pointbrush(x, y);
        bgmfd.fillRect(drawpoint[0]-2, drawpoint[1]-2, 4, 4);
        repaint();
    }
    
    public void initialprint(double outflowi,double inflowiinc, double inflowbinc, int trmaxforward,double truckrate){
        bgmfd.setColor(Color.black);
        bgmfd.drawString("Inner outflow chance rate(#/6.67m)="+outflowi+"   Inner inflow increasing rate(*.225veh/hr)="+inflowiinc,15,640);
        bgmfd.drawString("Boundary inflow increasing rate(*22.5veh/hr)="+inflowbinc,15,655);
        bgmfd.drawString("TruckSpeed(km/hr)="+(trmaxforward*15)+"   TruckRate="+truckrate,15,670);
        bgmfd.setColor(Color.red);
    }

}

 