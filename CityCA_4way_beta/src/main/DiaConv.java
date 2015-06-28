package main;

public class DiaConv{
    double maxx, maxy, xstart, ystart, xsize,ysize;
    public DiaConv(double maxx, double maxy){
        xstart = 100; ystart =650;
        xsize = 600; ysize = 600;
        this.maxx=maxx;this.maxy=maxy;}
 
    public DiaConv(double maxx, double maxy, double xstart,double ystart,
            double xsize,double ysize){
        this.xstart = xstart; this.ystart =ystart;
        this.xsize = xsize; this.ysize = ysize;
        this.maxx=maxx;this.maxy=maxy;
    }
    
    public int[] pointbrush(double x, double y){
        int drawx=(int)(xstart+x/maxx*xsize);
        int drawy=(int)(ystart-y/maxy*ysize);
        int[] pointloc=new int[2];
        pointloc[0]=drawx;
        pointloc[1]=drawy;
        //System.out.print("double: "+x+","+y+" int: "+drawx+","+drawy+"\n");
        return pointloc;
    }
   public int[] linebrush(double slope, double interc){
       //System.out.print("slope "+slope+" interc "+interc+"\n"); 
       int xx1,xx2,yy1,yy2;       
        double x1=0;
        double y1=(double)(interc);
        double x2=maxx;
        double y2=(double)(x2*slope+interc);
        if (y1>maxy){y1=(double)maxy;x1=(double)((maxy-interc)/slope);}
        if (y2>maxy){y2=(double)maxy;x2=(double)((maxy-interc)/slope);} 
        xx1=(int)(xstart+x1/(double)(maxx)*xsize);
        yy1=(int)(ystart-y1/(double)(maxy)*ysize);
        xx2=(int)(xstart+x2/(double)(maxx)*xsize);
        yy2=(int)(ystart-y2/(double)(maxy)*ysize);
        int[]lineloc=new int[4];
        //System.out.print("xx1 "+xx1+" xx2 "+xx2+" yy1 "+yy1+" yy2 "+yy2+"\n"); 
        lineloc[0]=xx1;lineloc[1]=yy1;lineloc[2]=xx2;lineloc[3]=yy2;
        
        return lineloc;
    }
}
