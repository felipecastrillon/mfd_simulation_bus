package main;

import javax.swing.*;
import java.awt.*;
import java.awt.Font;

public class Diagram extends JPanel{
    Image buffer;
    public Graphics2D bgmfd;
    int xwidth=820,yheight=780;
    String unitlabel;
    double maxx,maxy;
    double markx[]=new double[5],marky[]=new double[11];
    public DiaConv plots;
    boolean initiated=false;
    
    Diagram(String unitlabel,double maxx,double maxy){
        setSize(xwidth,yheight);
        this.unitlabel=unitlabel;
        this.maxx=maxx; this.maxy=maxy;
        for (int i=0;i<5;i++){markx[i]=(double)Math.floor(100*maxx/5*(i+1))/100;}
        for (int i=0;i<=10;i++){marky[i]=(double)Math.floor(100*maxy/10*i)/100;}
        plots=new DiaConv(maxx,maxy);
    }
    public void inipaint(){
        buffer=createImage(xwidth, yheight);
        bgmfd=(Graphics2D)buffer.getGraphics();
        bgmfd.setColor(Color.white);
        bgmfd.fillRect(0,0,xwidth,yheight);
        
        bgmfd.setColor(Color.black);
        //xaxis line
        bgmfd.drawLine(100,650,700,650);
        //yaxis line
        bgmfd.drawLine(100,50,100,650);
        
        bgmfd.setColor(Color.LIGHT_GRAY);
        //horizontal gridlines
        bgmfd.drawLine(100,590,700,590);
        bgmfd.drawLine(100,530,700,530);
        bgmfd.drawLine(100,470,700,470);
        bgmfd.drawLine(100,410,700,410);
        bgmfd.drawLine(100,350,700,350);
        bgmfd.drawLine(100,290,700,290);
        bgmfd.drawLine(100,230,700,230);
        bgmfd.drawLine(100,170,700,170);
        bgmfd.drawLine(100,110,700,110);
        bgmfd.drawLine(100,50,700,50);

        //vertical gridlines
        bgmfd.drawLine(220,50,220,650);
        bgmfd.drawLine(340,50,340,650);
        bgmfd.drawLine(460,50,460,650);
        bgmfd.drawLine(580,50,580,650);
        bgmfd.drawLine(700,50,700,650);
        
        //set axis labels
        Font gfont=new Font("Rockwell",Font.BOLD,15);
        bgmfd.setFont(gfont);
        bgmfd.drawString(unitlabel, 10, 30);
        bgmfd.drawString("Density", 375, 690);
        bgmfd.rotate(-Math.PI/2,70,365);
        bgmfd.drawString("Flow", 60, 365);
        bgmfd.rotate(Math.PI/2,70,365);
        
        //set gridline labels
        bgmfd.setColor(Color.black);
        bgmfd.drawString(Double.toString(marky[0]),75,670);
        bgmfd.drawString(Double.toString(marky[1]),75,610);
        bgmfd.drawString(Double.toString(marky[2]),75,535);
        bgmfd.drawString(Double.toString(marky[3]),75,475);
        bgmfd.drawString(Double.toString(marky[4]),75,415);
        bgmfd.drawString(Double.toString(marky[5]),75,355);
        bgmfd.drawString(Double.toString(marky[6]),75,295);
        bgmfd.drawString(Double.toString(marky[7]),75,235);
        bgmfd.drawString(Double.toString(marky[8]),75,175);
        bgmfd.drawString(Double.toString(marky[9]),75,115);
        bgmfd.drawString(Double.toString(marky[10]),75,55);
        bgmfd.drawString(Double.toString(markx[0]),210,675);
        bgmfd.drawString(Double.toString(markx[1]),330,675);
        bgmfd.drawString(Double.toString(markx[2]),450,675);
        bgmfd.drawString(Double.toString(markx[3]),570,675);
        bgmfd.drawString(Double.toString(markx[4]),690,675);
        bgmfd.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    }
   
    public void paint(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.drawImage (buffer, 0, 0, null);
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
    public void plotline(double m, double b){
        int linepoint[]=plots.linebrush(m,b);
        bgmfd.drawLine(linepoint[0], linepoint[1], linepoint[2], linepoint[3]);
        //outfile.println(m+ ",.."+ b);
        repaint();
    }
       
    //public void paintupdate(double x, double y){}
    //public void paintupdate(double x, double y, Color c){}
    public synchronized void estimation(int length, int offset, int green, int cycle,int stnum,double tunit,double distunit,double pbus){}
    public void initialprint(double outflowi,double inflowiinc, double inflowbinc, int trmaxforward,double truckrate){}
    public void finalprint(){}
    public void cuts(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus){}
    public void geroliminis_simulation(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus){}     
    public void shortpath1(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus,double pstop,double dwell){}
    public void shortpath2(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus){}
    public void shortpath3(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus){}
    public void shortpath4(int length, int offset, int green, int cycle, int stnum,double tunit,double distunit,double pbus){}
    public void update(Graphics g){
        paint(g);
    }
    public void colorchange(Color c){
        bgmfd.setColor(c);
    }
}