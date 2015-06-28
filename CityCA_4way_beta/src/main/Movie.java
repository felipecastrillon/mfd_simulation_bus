package main;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.text.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

//import java.awt.image.*;

public class Movie extends JPanel implements MouseListener , Serializable {

    private Network net;
    private String xLabel;
    private String yLabel;
    private int xsize, ysize, n_clicks=-1, maxheight,maxwidth;
    private int tj = 0, counter =0, n0 = 0;
    private Color vColor;
    private Graphics2D bg = null;
    private Image buffer;
    private String dir = "movie/";
    private  int dotsize = 2, ia_sp=-1,ja_sp=-1,ib_sp=-1,jb_sp=-1,median=0,
        interwd,b, carf, xm, ym, L,MAXL, B,LN, bx=0, by=0;
    private double xf, yf, xa, xb, ya, yb, dx, dy;
    public List<StreetSegment> spList= new ArrayList<StreetSegment>();
    public List<Integer> blheight= new ArrayList<Integer>();
    public List<Integer> blwidth= new ArrayList<Integer>();
    public boolean chkCarColor = false;
 // The X-coordinate and Y-coordinate of the last click.
     int xpos;
     int ypos;

    public Movie () {

    }
    
    public synchronized void pinta(int i){
        pintaCalle(bg,i);
        prt(Global.TimeToString(CityApplet.t), 40, 60);
        repaint();
        counter++;
    }
    
    public void setxLabel(String xl){
        xLabel = xl;
    }
    
    public void setyLabel(String yl){
        yLabel =  yl;
    }
    
    public void setNet(Network n){
        net = n;
    }
    
    public void ini(){
        //if (buffer == null){
            xsize = 650;
            ysize = 650;
            buffer = createImage(xsize,ysize);
            bg = (Graphics2D)buffer.getGraphics();
            bg.setColor (Color.black);
            bg.fillRect (0, 0, xsize, ysize);
            bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        //}
        dotsize = 4;
        b=4;
        carf=2;
        median = 2;
        xm = dotsize; ym=dotsize;
        L = CityApplet.length;
        B = net.BLOCKS;
        MAXL = net.maxlen;
        maxheight = net.getMaxHeight();
        maxwidth = net.getMaxWidth();
        blwidth = net.getBlockWidths();
        blheight = net.getBlockHeights();
        LN = StreetSegment.LANES;
        interwd = LN*2+median;
        xf = ((double)xsize/(double)(maxwidth + (B+1)*interwd));
        yf = ((double)ysize/
                (double)(maxheight + (B+1)*interwd));
        
        addMouseListener(this);
        
    }

    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        if (buffer != null) g2.drawImage (buffer, 0, 0, null);
    }

    private void  pintaCalle (Graphics2D g, int ii){
        Color myGray =  new Color((int)(255*0.97),(int)(255*.97),(int)(255*0.97));
//BORRA
        //g.setColor(new Color(204, 204, 255));
        //g.setColor(Color.black);
        //g.fillRect(0, 0, xsize, ysize);
//PINTA BLOCKS
        //g.setColor(myGray);
        //for (int i = 0; i < B; i++) for (int j = 0; j < B; j++)
        //   g.fillRect(3*xm+(int)(i*(2*b+xf*L)),3*ym+(int)(j*(2*b+yf*L)),(int)(xf*L)-b,(int)(yf*L)-b);

        //xb = 3*xm + vb.j*(2*b+xf*L);
        //yb = 3*ym + vb.i*(2*b+yf*L);
        
//pintaAutos
        for(StreetSegment s : net.graph.edgeSet()){
            Node va=null, vb=null;
            va = net.graph.getEdgeSource(s);
            vb = net.graph.getEdgeTarget(s);
            setXYab(va, vb, g, s.dir);
            pintaAutos(s, g, ii);
            //pintaArrowsNet(cell, g);
        }
//        if(str.length()>3){
//            Node va=null, vb=null;
//            DecimalFormat df = new DecimalFormat("#");
//            va=net.getNode(Integer.parseInt(str.substring(0,1)),Integer.parseInt(str.substring(1,2)));
//            vb=net.getNode(Integer.parseInt(str.substring(2,3)),Integer.parseInt(str.substring(3,4)));
//        pintaSP(CityApplet.txtAB.getText());
        //if (ib_sp!=-1)pintaSP(ia_sp,ja_sp,ib_sp,jb_sp);
    }

    private void pintaAutos(StreetSegment s, Graphics2D g, int ii) {
          
        int xl=0,yl=0,xoff=0,yoff=0;    
        for (int k = 0; k < s.LENGTH; k++) {
            for (int l = 0; l < s.LANES; l++){
                
                if(s.dir==Global.EW){
                    xl = -1;
                    yoff = -(median/2 + l); 
                    xoff =  -interwd/2;
                }
                else if(s.dir==Global.WE){
                    xl = 1;
                    yoff = (median/2 + l); 
                    xoff = interwd/2;
                }
                else if(s.dir==Global.NS){
                    yl = 1;
                    xoff= -(median/2 + l); 
                    yoff = interwd/2;
                }
                else if(s.dir==Global.SN){
                    yl = -1;
                    xoff= (median/2 + l); 
                    yoff = -interwd/2;
                }
                
                if(s.cell[k][l]>-1){
                    int kk = k + Math.min(ii, s.cell[k][l]);
                    if (s.type[k][l] == 0){ //if it is a car
                        if(chkCarColor)g.setColor(
                                getColorBySpeed(s.cell[k][l]));
                        else g.setColor(s.CarColor[k][l]);
                        g.fillRect((int)(xf*(xa + xoff + xl*k)),
                                (int)(yf*(ya + yoff + yl*k)),dotsize, dotsize);
                    }//if(CityApplet.chkCarColor.isSelected())g.setColor(Global.CarColor(s.cell[k])); else g.setColor(s.CarColor[k]);
                   else if (s.type[k][l] == 1){//if it is a bus
                        if(chkCarColor)g.setColor(new Color(246, 131, 44)); 
                        else g.setColor(s.CarColor[k][l]);
                        g.fillRect((int)(xf*(xa-(interwd/2+k))+b),
                                (int)(yf*(ya-median/2-l)+b),dotsize, dotsize);
                        if(chkCarColor)g.setColor(Color.PINK); 
                        else g.setColor(Color.BLUE);
                        g.fillRect((int)(xf*(xa-(interwd/2+k-2))+b),
                                (int)(yf*(ya-median/2-l)+b),dotsize, dotsize);
                        g.fillRect((int)(xf*(xa-(interwd/2+k-1))+b),
                                (int)(yf*(ya-median/2-l)+b),dotsize, dotsize);
                   }  
                   else if(s.type[k][l] == 2){
                        if(chkCarColor)g.setColor(Global.CarColor(s.cell[k][l])); 
                        else g.setColor(s.CarColor[k][l]);
                        g.setColor(s.CarColor[k][l]);
                        g.fillRect((int)(xf*(xa + xoff + xl*k)),
                                (int)(yf*(ya + yoff + yl*k)),dotsize, dotsize);
                   }
                   else{
                       if(chkCarColor)g.setColor(Color.ORANGE); 
                       else g.setColor(s.CarColor[k][l]);
                       g.fillRect((int)(xf*(xa-(interwd/2+k))+b),
                                (int)(yf*(ya-median/2-l)+b),dotsize, dotsize);
                       System.out.print("error\n");
                       System.exit(0);
                   }
                }else{
                    g.setColor(Color.darkGray);
                    g.fillRect((int)(xf*(xa + xoff + xl*k)),
                            (int)(yf*(ya + yoff + yl*k)),dotsize, dotsize);
                }
                //else{System.out.print("error!\n");}
            }
        }
            

        //PINTA LIGHTS
        g.setColor(s.SignalColor);
        if((s.dir==Global.EW)||(s.dir==Global.WE)){
            g.fillRect((int)(xf*xb),(int)(yf*yb-2*b),b,5*b);
            //g.fillRect((int)(xf*xa),(int)(yf*ya-2*b),b,5*b);
        }else{
            g.fillRect((int)(xf*xb-2*b),(int)(yf*yb),(int)(5*b),(int)(b));
            //g.fillRect((int)(xf*xa-2*b),(int)(yf*ya),(int)(5*b),(int)(b));
        }
        //PARKS
//       g.setColor(new Color(150,200,150));
//        if(!cell.open)g.fillRect((int)(x0-xf*L),(int)(y0+b),(int)(2*xf*L+ b),(int)(yf*L)-b);

    }
   
    private void pintaArrows(StreetSegment s, Graphics2D g) {
        if(s.access)g.setColor(Color.black); else g.setColor(Color.blue);
        Global.drawArrow(g, (int)xa+2*bx,(int)ya+2*by,(int)xb+2*bx,(int)yb+2*by,(float)0);
    }

    private void setXYab(Node va, Node vb, Graphics2D g, int dir) {
        int ay = va.i, ax = va.j, by = vb.i, bx = vb.j; 
        
        //change logic if it is a ring network
        if(dir==2 && va.j == 0){
            ax = Global.blocks;
        }else if(dir==4 && va.i == 0){
            ay = Global.blocks;
        }
        
        //get intersection coordinates
        xa = interwd/2+ax*(interwd) + blwidth.get(ax); 
        ya = interwd/2+ay*(interwd) + blheight.get(ay); 
        xb = interwd/2+bx*(interwd) + blwidth.get(bx); 
        yb = interwd/2+by*(interwd) + blheight.get(by);
    }

    private void pintaArrowsNet(StreetSegment s, Graphics2D g) {
        /*Node va=null, vb=null;
        va = net.graph.getEdgeSource(s);
        if(s.nextS!=null){
            vb = net.graph.getEdgeTarget(s.nextS);
            setXYab(va, vb, g, s.dir);
            g.setColor(Color.ORANGE);
            Global.drawArrow(g, (int)xa+2*bx,(int)ya+2*by,(int)xb+2*bx,(int)yb+2*by,(float)0);
        }*/
    }

    public void prt(String str, int x, int y) {
        bg.setColor(Color.white);
        bg.fillRect(x, y-10, 50, 10);
        bg.setColor(Color.black);
        bg.drawString(str,x,y);
    }
    
    private void pintaSP(int ia, int ja, int ib, int jb) {
//System.out.println("i, j"+ia+"|"+ja+"|"+ib+"|"+jb);
        Node va=null, vb=null;
        DecimalFormat df = new DecimalFormat("#");
        va=net.getNode(ia,ja);
        vb=net.getNode(ib,jb);
        spList = va.sp.getPathEdgeList(vb);
        pintaNode(va);
        pintaNode(vb);
        for(StreetSegment s : spList){
            setXYab(s.va, s.vb, bg, s.dir);
            pintaArrows(s, bg);
            //bg.drawString(s.va.name,(int)xa,(int)ya);
            String c = "" + df.format(s.getSpeed());
            bg.drawString(c,(int)(xa+xb)/2,(int)(ya+yb)/2);
        }
    }

    private void pintaNode(Node v) {
        double x = 3*xm + v.j*xf*L-3*b;
        double y = 3*ym + v.i*yf*L-3*b;
        bg.setColor(Color.MAGENTA);
        bg.drawOval((int)x, (int)y, 6*b, 6*b);
    }
    
    private int getCoordinates(int ia, int ja , int ib, int jb, int lane, 
            int ln, int celln, int dir){
        int centerx, centery,xcrd,ycrd;
        
        if (dir == 1){
            centerx = blwidth.get(ia);
            centery = blheight.get(ja);
            xcrd = interwd/2 + celln + centerx;
            ycrd = centery + ln + interwd/2;
        }else if (dir == 2){
            centerx = blwidth.get(ia);
            centery = blheight.get(ja);
            xcrd = interwd/2 + celln + centerx;
            ycrd = centery + ln + interwd/2;
        }
        
        return 0;
    }
    
    private Color getColorBySpeed(int speed){
        if (speed == Global.car_spd){
            return Color.green;
        }else if (speed == 0){
            return Color.red;
        }else{
            return Color.yellow;
        }
    }
    
    public void takePicture(int t){
        File outputfile1=new File("snapshot"+t+".jpg");
        paint(bg);
        BufferedImage image1 = (BufferedImage)(buffer);

        try{
            ImageIO.write(image1,"jpg",outputfile1);
        }
        catch (IOException ioe){
        }
    }
        
    public void mouseClicked(MouseEvent e) {
  // Save the coordinates of the click lke this.
      xpos = e.getX();
      ypos = e.getY();
      n_clicks++;
      if(n_clicks%2==0){
          ia_sp = (int)((double)(ypos - 3*xm)/(double)(yf*L)+0.5);
          ja_sp = (int)((double)(xpos - 3*xm)/(double)(xf*L)+0.5);
      }else{
          ib_sp = (int)((double)(ypos - 3*xm)/(double)(yf*L)+0.5);
          jb_sp = (int)((double)(xpos - 3*xm)/(double)(xf*L)+0.5);
          pintaSP(ia_sp,ja_sp,ib_sp,jb_sp);
      }
    }

    public void mousePressed(MouseEvent e) {}
    
    public void mouseReleased(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    private void restart(){
        
    }
}

