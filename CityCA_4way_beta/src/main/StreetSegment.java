package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.Daganzo;
import models.Simulator;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;


public class StreetSegment extends DefaultWeightedEdge implements java.io.Serializable{
    public static double[] inflow = {0,0};
    public double[] speed_ma, tt_ma;
    public static double exitRate=1;
    public static int LANES; // in # cells
    public int LENGTH;
    public int[][] cell, cell_new, type, type_new,bus_stp,deptime,linktt;
    private String[][] links;
    private int[] wait;
    public int begin_station,end_station; //spacing in # cells
    private double dens[] = {0,0,0,0}, flow[]= {0,0,0,0}, speed[]= {0,0,0,0},effdens;
    private double outflow[] = {0,0,0,0}, incnt[] = {0,0,0,0};
    private double totdist[] = {0,0,0,0}, tottime[] = {0,0,0,0};
    public Color SignalColor=Color.GREEN;
    public Color[][] CarColor;
    public boolean access=false,yadoblo[]={true,true}, open=true, 
            trigger[]={false,false};
    public int kk,dir,nOutLinks,lane,turndir;
    private Simulator sim = new Daganzo();
    public int iMB=-1, vMB=-1, n_ma=0, ttind = 0; 
    public Node va,  vb, CarDest[][],CarOrig[][],temp;
    private GraphPath Path[][];
    public StreetSegment[] nS;
    int[] integerArray = new int[10];
    public StreetSegment nextS,nextSeg[][];
    private Network net;
    private static double green = Node.NgocEW*Node.Ncycle, cycle = Node.Ncycle;
    public static double pbus = 0.0,pentry,realpbus;
    public static int busl = 3,busalllanes =1,busstoptime = 10;
    public static double pbusstop = 0.5,pturn = 0.1;
    public static double kveh = 0.35, busprop = 0.1, preroute=0.0;
    public int[] stopcnt;
    public String sname;
    public int cnt1=0, cnt2=0, cnt3=0,cnttt = 0,totaltt=0;
    public int lnchgdir =1 ,gapcurr;
    public static int gapaccept = 1, lnchgspd = 2, targetln,lanechanging = 3;
    public static int gapneg = Global.OMEGA[0], gappos = 1, lookahead = 6, 
            deltapos = 0, deltaneg = 0;
    public boolean chgln;
    public double turncnt,noturncnt,traveltime;
    public static int typeofinflow = 1; 
    public double lastt = 0,totheadway = 0,hwcount=0, vehcnt,nextbust[];
    public double pbus_mod = 0,tlastbus=0;
        public boolean override[];
    public static double tvheadway = 0;//seconds
    public static int resetdt = 1, speed_dt = 360,rrwait=9999,ma_cnt=1;
    public static double  meanrrt=1.0;
    DijkstraShortestPath dsp;

    public StreetSegment(){
    }
  
    public StreetSegment(Network n, Node vaa, Node vbb, int L, int d,int ln){// (double density, double Global.deltaX, Simulator sim) {
        sname = vaa.name + "to" + vbb.name + "dir" + d;
        net = n;
        va = vaa;
        vb = vbb;
        LENGTH=L;
        dir=d;
        turndir=d;
        LANES = ln;
        override= new boolean[LANES];
        nextbust= new double[LANES];
    }
    
    public void ini(){
        //System.out.print("initializing " + sname + "\n");
        traveltime = LENGTH/Global.car_spd +  (Global.cycle-Global.green)/2;
        kk = ((dir==Global.EW)||(dir==Global.WE))?0:1;
        setSimulator (sim);
        type = new int[LENGTH+1][LANES];
        type_new = new int[LENGTH+1][LANES];
        cell  = new int[LENGTH+1][LANES];
        wait  = new int[LANES];
        cell_new  = new int[LENGTH+1][LANES];
        bus_stp  = new int[LENGTH+1][LANES];
        stopcnt = new int[LANES];
        CarColor = new Color[LENGTH][LANES];
        CarDest = new Node[LENGTH][LANES];
        CarOrig = new Node[LENGTH][LANES];
        Path = new GraphPath[LENGTH][LANES];
        deptime = new int[LENGTH][LANES];
        links = new String[LENGTH][LANES];
        linktt = new int[LENGTH][LANES];
        speed_ma = new double[speed_dt];
        tt_ma = new double[ma_cnt];
        nS = new StreetSegment[LANES];
        nextSeg = new StreetSegment[LENGTH][LANES];
        for (int i = 0; i < speed_ma.length; i++) speed_ma[i]=0*Global.OMEGA[0];
        for (int i = 0; i < ma_cnt; i++) tt_ma[i]=LENGTH/Global.OMEGA[0]+traveltime;
        
        // initialize cars and buses on the road
        for (int i=0;i<LENGTH;i++){ 
           for(int l=0; l<LANES;l++){
                stopcnt[l] = busstoptime;
                if (Math.random()<kveh){
                    if (Math.random()<busprop && 
                       //buses operate every other corridor. This can be changed
                        (((dir==1 || dir ==2) && va.i%2 ==0)||  
                         ((dir==3 || dir ==4) && va.j%2 ==0))
                            ){
                        type[i][l] = 2;
                        CarColor[i][l]=Color.MAGENTA;
                    }else{
                        type[i][l] = 0;
                        CarColor[i][l]= Color.yellow;
                    }
                    cell[i][l] = 0;
                    bus_stp[i][l] = 0;
                    CarDest[i][l] = getCarDest(type[i][l]);
                }else{
                    cell[i][l] = -1;
                    type[i][l] = -1;
                    bus_stp[i][l] = 0;
                }
                wait[l]=0;
                
               cell_new[i][l] = -1;   
               type_new[i][l] = -1;
               nextbust[l] = tvheadway;
               trigger[l] = false;
               CarOrig[i][l]=null;
               deptime[i][l]=0;
               linktt[i][l]=0;
               links[i][l]="";
               Path[i][l]=null;
           }
        }
        
        //set bounds for bus station
        begin_station = (int)Math.round((double)Global.length/2.0)-
        (int)((double)Math.max(Global.bus_spd,Global.bus_lg)/2.0);
        begin_station = Math.max(begin_station,gapneg+deltaneg+Global.bus_lg);
        end_station = (int)Math.round((double)Global.length/2.0)+
        (int)((double)Math.max(Global.bus_spd,Global.bus_lg)/2.0);
  }
    
    public void setSimulator (Simulator sim) {
        this.sim = sim;
        sim.init (Global.OMEGA, LENGTH, Global.deltaX);
  }
    
    public void boundaries(double busprop,boolean blength){

    for (int l=0;l<LANES;l++){
        int tt = CityApplet.t;
        boolean blocked = false;
        int checklen = Global.bus_lg;
        if (LENGTH<checklen){
            checklen = LENGTH;
        }
        if (!(access) || cell[0][l]>-1){// ||
              // (tt>=va.tIniGreen[-1*(kk-1)])&&(tt<va.tEndGreen[-1*(kk-1)])){
        //if no entry access or first cell is blocked or red signal.
            blocked = true;
        }
        for (int i = 1;i<checklen;i++){//if first cell is blocked by buses
            if ((cell[1][l]>-1 && type[1][l]==1)){
                blocked = true;
            }
        }

        if (blocked==false){

            //System.out.print("street inflow "+inflow[kk]+"\n");
            if (typeofinflow == 2){//headway input
                pbus_mod=0.0;
                if (busalllanes == 1 && tvheadway>0){
                    //get headway
                    if (CityApplet.t>=nextbust[l] && override[l] ==false){
                        double modhw,greenthw,ngreencyc,afterred,beforered,
                                nexthw;
                        modhw=tvheadway*green*LANES/cycle;
                        greenthw = -Math.log(Math.random())*modhw;
                        beforered = green - CityApplet.t%cycle;
                        afterred = (greenthw-beforered)%green;
                        ngreencyc = Math.floor((greenthw-beforered-afterred)/green);
                        if (beforered < greenthw){
                            nexthw = beforered+(cycle-green)+ ngreencyc*cycle+afterred;
                        }else{
                            nexthw = greenthw;
                        }
                        nextbust[l] = CityApplet.t + nexthw;
                        override[l] = true;
                    }       
                }
                if (busalllanes != 1 && tvheadway>0){
                    //get headway
                    if (CityApplet.t>=nextbust[l] && l==0 && override[l] ==false){
                        double modhw,greenthw,ngreencyc,afterred,beforered,
                                nexthw;
                        modhw=tvheadway*green/cycle;
                        greenthw = -Math.log(Math.random())*modhw;
                        beforered = green - CityApplet.t%cycle;
                        afterred = (greenthw-beforered)%green;
                        ngreencyc = Math.floor((greenthw-beforered-afterred)/green);
                        if (beforered < greenthw){
                            nexthw = beforered+(cycle-green)+ ngreencyc*cycle+afterred;
                        }else{
                            nexthw = greenthw;
                        }
                        nextbust[l] = CityApplet.t + nexthw;
                        override[l] = true;
                    }                    
               }
            }


            if (Math.random() < inflow[kk]){
                cell_new[0][l] = 0;
                vehcnt ++;
                int i = (int)(Math.random()*net.BLOCKS+.5);
                int j = (int)(Math.random()*net.BLOCKS+.5);
                //System.out.print("i,j "+i+","+j+"\n");
                //double pbus_mod = -(busprop-pbus)*10 + pbus;
                if (typeofinflow == 0){
                   if (busalllanes == 1){                   
                       pbus_mod = pbus;
                   }else{
                       pbus_mod = pbus*2;
                   }
                }
                else if (typeofinflow == 1){
                    pbus_mod = -(busprop-pbus)*10 + pbus;
                }
//                    CityApplet.outfi.println(busprop);
                if ((Math.random() <= pbus_mod || override[l]==true) && 
                        (busalllanes == 1|| l == 0)){
                    override[l] = false;
                    CarColor[0][l]= Color.WHITE;
                    lastt = CityApplet.t;
                    if (hwcount > 0){ 
                        totheadway += (CityApplet.t - lastt);
                    }
                    hwcount++;

                    if (blength == true)
                        type_new[0][l] = 1;
                    else    
                        type_new[0][l] = 2;
                    //only boundaries for buses
                    while(true){
                        if (i == 0 || j==0){
                            break;
                        }else{
                          i = (int)(Math.random()*net.BLOCKS+.5);
                          j = (int)(Math.random()*net.BLOCKS+.5);
                        }
                    }
                    //determine if bus stops on first block
                    if (decideToStop(vb.j)==true){
                        bus_stp[0][l]=-1;
                    }

                    //CarDest[0][l] = net.getNode(va.i,va.j);

                 }else{ 
                    //if (Math.random() > pbus_mod){
                    CarColor[0][l]=Color.green;
                    type_new[0][l] = 0;
                    //type[0][l] = 2;
                    //CarDest[0][l] = net.getNode(i,j);
                 }

                CarDest[0][l] = getCarDest(type_new[0][l]);
                incnt[type_new[0][l]+1]++; 
            }
        }

        //signal timing
        vb.setSignalTiming(tt);
        if ((tt>=vb.tIniGreen[kk])&&(tt<vb.tEndGreen[kk])){
            cell[LENGTH][l]=-1;
            SignalColor = Color.GREEN;
        } else {
            cell[LENGTH][l]=0;
            SignalColor = Color.RED;
        }
    }
}

    public void updateSpeeds(){
        int gap=9999,j,i,l;
        boolean leadingveh;
        for (i = 0;i < LENGTH;i++){
            for (l = 0;l < LANES;l++){
                leadingveh=false;

                //if cell is full   
                if(cell[i][l] > -1){
                    
                    // searching for the vehicle ahead on link      
                    gap=getFrontGap(i,l,cell,type,LENGTH);
                    
                    //if leading vehicle close to the intersection and green light
                    if (gap==9999 && LENGTH-i-1<Global.car_spd){ 
                        leadingveh=true;
                        //if car has just arrived at the link, get next link                    
                        if (yadoblo[l]){                       
                            yadoblo[l]=false;
                        }
                        
                        //get new segment based on car destination
                        if (type[i][l]>0){//if it is a bus 
                                nS[l] = getNextBusSegmentRingRoad(i,l);
                        }else{                   
                                if(Path[i][l]==null || //newpath == null ||
                                    Math.random() < 1.0/meanrrt){

                                    if(Path[i][l]!=null &&
                                            Path[i][l].getEdgeList().size() < 3){
                                        Path[i][l] = getNewPath(vb,CarDest[i][l],Path[i][l]);
                                    }else{
                                        Path[i][l] = getNewPath(vb,CarDest[i][l],null);
                                    }
                                        
                                }
                                nS[l] = getNextSegmentFromPath(Path[i][l],vb);
                                //nS[l] = getNextSegment(i,l,vb);
                                
                                if(nS[l] != null){
                                    //get gap
                                    gap=Math.min(LENGTH-i-1+getFrontGap(-1,l,nS[l].cell,
                                            nS[l].type,nS[l].LENGTH),
                                            9999);
                                }else{
                                    trigger[l]=true;
                                }
                        }
                        
                        //if waiting for too long, change path
                        if (i == LENGTH-1 && wait[l]>rrwait
                                && type[i][l]==0){
                            //CarDest[i][l] = getCarDest(type[i][l]);
                            Path[i][l] = getNewPath(vb,CarDest[i][l],null);
                            nS[l] = getNextSegmentFromPath(Path[i][l],vb);
                            //CarOrig[i][l]=null;
                        }
                    }
                   //bus stops
                    if (type[i][l]!=0){  
                        boolean stop = getBusStop(i,l,Math.min(gap,Global.bus_spd)); 
                        if (stop){gap=0;}
                        //if bus is on inside lane but has to make a stop
                        if (l!=0 && bus_stp[i][l]<=-1 && i>Global.car_spd
                                && begin_station+Global.bus_spd >= i){
                            gap=0;
                            bus_stp[i][l]--;
                            if (bus_stp[i][l] <= -2*Global.dwell){
                                bus_stp[i][l]=0;
                            }
                        }
                    }
                   cell[i][l] = gap;
                }
            }
        }
    }
    
    public void laneChange(){
    //every timestep change directions for lane changing  
    if (lnchgdir == 1){
        lnchgdir  = -1;
    }else{
        lnchgdir  = 1;
    }

    for (int l = 0;l < LANES;l++){ //for every lane 
         targetln = l + lnchgdir; //target lane for lane change
         if (targetln <= -1 || targetln >= LANES){
             targetln = l - lnchgdir;
         } 
         
         for (int i = 0;i < LENGTH;i++){ //for every cell
             if (cell[i][l] >= 0){
                 chgln = false; //default
                 boolean incentive = false; //incentive to change lane
                 gapcurr = cell[i][l]; // current gap
                 
                 if (i+Global.car_spd < LENGTH)//no lane changes near intersection
                 {       
                    //lane changing incentives for all vehicles
                    double ffs = Global.car_spd;
                    if (type[i][l]==1||type[i][l]==2){
                        ffs = Global.bus_spd;
                    }
                    if (gapcurr <=ffs){
                           incentive = getVelBasedIncentive(i,targetln);
                    }
                    
                    //override incentives for buses 
                    if (type[i][l]>0){
                        if (l==0){//if outside lane
                            
                            if (bus_stp[i][l] != 0){//making a stop
                                incentive=false;
                            }else if (cell[i][l]==0 && incentive == true){
                                //if it just got done making a bus stop
                                incentive = false;
                                for (int m=1; m<=Global.bus_spd;m++){
                                    if (cell[i+m][l]>-1 && 
                                       (type[i+m][l] == 1 || type [i+m][l]==2)){
                                        incentive = true;
                                        break;
                                    }
                                }
                            }
                        }else{//if inside lanes
                            if(bus_stp[i][l]!=0){
                                incentive=true;
                            }
                        }
                    }
                     //check gaps to make lane change if incentive is true
                     if (incentive == true){
                         //get length of vehicle trying to make a lane chg
                         int veh_lg=0;
                         if (type[i][l]==0){
                             veh_lg = Global.car_lg;
                         }else if (type[i][l]==1||type[i][l]==2){
                             veh_lg = Global.bus_lg;
                         }
                         //check that there is enough forward and backward gap
                         int backgap = i-gapneg-deltaneg-veh_lg;
                         int fwdgap = i + gappos+deltapos;
                         if (backgap >= 0 && fwdgap < LENGTH){
                            int safety_gap = getFrontGap(backgap,targetln,cell,
                                    type, LENGTH);
                            if (safety_gap >= gapneg+gappos+deltapos+deltaneg+veh_lg)
                               chgln = true;
                         }
                     }                       
                 }
                 //update vehicles if lane changing conditions are true
                 if (chgln == true){
                     //change lanes    
                     cell[i][targetln] = 0;
                     cell[i][l] = -1;
                     type[i][targetln] = type[i][l];
                     type[i][l] = -1;
                     CarColor[i][targetln]=CarColor[i][l];
                     CarDest[i][targetln] =CarDest[i][l];
                     CarOrig[i][targetln] =CarOrig[i][l];
                     deptime[i][targetln] =deptime[i][l];
                     CarOrig[i][targetln] =CarOrig[i][l];
                     links[i][targetln] =links[i][l];
                    if (bus_stp[i][l]<=-1){
                        bus_stp[i][targetln] =bus_stp[i][l];
                        bus_stp[i][l]=0;
                    }
                     //update new gaps 
                     int gap = 9999;
                     cell[i][targetln] = getFrontGap(i,targetln,cell,type,
                             LENGTH);
                 }
             }
         }
     }

}
    
    public void updateSpeeds2(){
      for (int i = 0;i < LENGTH;i++){
          for (int l = 0;l < LANES;l++){
              if (cell[i][l] > -1){ // if cell is not empty, update
                 // System.out.print(cell[i][l] + "cell\n");
               cell[i][l] = sim.updateSpeed (cell[i][l], cell[i][l], Global.slowdown,Global.lambda,type[i][l]);
              }
              //keep track of vehicles being stuck waiting forever
              if (cell[LENGTH-1][l]==0 && cell[LENGTH][l]==-1){
                  wait[l]++;
              }else{
                  wait[l]=0;
              }
          }

      }

  }
    
    public void updatePos() { //System.out.print("updating pos\n");
      GraphPath temppath=null; Node tempcardest=null;
      //turncnt = 0; noturncnt = 0;
      for (int l=0;l<LANES;l++){
      int j,i=0;
      //for (i=0;i<LENGTH;i++){
      for (i=0;i<LENGTH;i++){
              j=i;
         if(cell[i][l] > 0){
             tottime[type[i][l]+1]++;               
              //update positions
                j = i+cell[i][l];
                if (j<LENGTH){// exchange the positions
                      cell_new[j][l] = cell[i][l];
                      type_new[j][l] = type[i][l];
                      CarColor[j][l]=CarColor[i][l];
                      CarDest[j][l] =CarDest[i][l];
                      CarOrig[j][l] =CarOrig[i][l];
                      deptime[j][l] =deptime[i][l];
                      Path[j][l] = Path[i][l];
                      links[j][l] =links[i][l];
                      linktt[j][l]=linktt[i][l]+1;
                      if (bus_stp[i][l]<=-1){
                          bus_stp[j][l] =bus_stp[i][l];
                          bus_stp[i][l]=0;
                      }
                      totdist[type[i][l]+1] += (j-i);                        
                }else{//j>LENGTH // if exiting the street segment
                    if(trigger[l]==true){
                        tempcardest = getCarDest(type[i][l]);
                        temppath = getNewPath(getRandomNode(tempcardest),tempcardest,null);
                        if(temppath==null){
                            System.out.println("here\n");
                        }
                        nS[l] = (StreetSegment) temppath.getEdgeList().get(0);
                    }  

                    if(nS[l]!=null){
                          int k=j%LENGTH;
                          int new_pos = 0;
                          //check for buses
                          int buschk = Global.bus_lg;
                          if(k>nS[l].LENGTH-1){
                              k = nS[l].LENGTH-1;
                          }
                          if(k+buschk-1 > nS[l].LENGTH-1){
                              buschk = nS[l].LENGTH-k-1;
                          }


                          for (int m=0;m<=k;m++){//check that cars are not crashing
                              for (int b=m; b<=m+buschk-1;b++){
                                  if (nS[l].cell[m][l]>-1 ||
                                          nS[l].cell_new[m][l] >-1
                                      ||  (nS[l].cell_new[b][l]>-1 && 
                                          nS[l].type_new[b][l]==1)
                                      ||  (nS[l].cell[b][l]>-1 && 
                                          nS[l].type[b][l]==1)  )  {
                                      k = m-1;
                                      break;
                                  }
                              }
                          }                         
                          if (k<0){ // if the next segments is occupied
                              cell_new[LENGTH-1][l] = cell[i][l];
                              type_new[LENGTH-1][l] = type[i][l];
                              CarColor[LENGTH-1][l]=CarColor[i][l];
                              CarDest[LENGTH-1][l] =CarDest[i][l];
                              CarOrig[LENGTH-1][l] =CarOrig[i][l];
                              deptime[LENGTH-1][l] =deptime[i][l];
                              Path[LENGTH-1][l] = Path[i][l];
                              links[LENGTH-1][l] =links[i][l];
                              linktt[LENGTH-1][l] =linktt[i][l]+1;
                              totdist[type[i][l]+1] += (LENGTH-1-i); 
                              int lenghtm1 = LENGTH-1;
                          }else{//update to next segment                                  
                              if(trigger[l]==true){ 
                                  if (CarOrig[i][l]!=null &&
                                      CarDest[i][l]!=null){
//                                        CityApplet.outfi.println(
//                                        CarOrig[i][l].name+","+
//                                        CarDest[i][l].name+","+deptime[i][l]+
//                                        ","+CityApplet.t+","+
//                                        CityApplet.ksd.getMinDist(CarOrig[i][l],
//                                                CarDest[i][l])+","+links[i][l]);
                                  }
                                  CarOrig[i][l]=nS[l].va;
                                  CarDest[i][l]=tempcardest;
                                  deptime[i][l]=CityApplet.t;
                                  if(CityApplet.t % 3000 > 500 && 
                                          CityApplet.t % 3000 < 510){
                                      CarColor[i][l] =Color.blue;
                                  }else{
                                      CarColor[i][l] = Color.yellow;
                                  }
                                  links[i][l]=nS[l].sname;
                                  trigger[l]=false;
                                  Path[i][l]=temppath;
                              }

                              nS[l].cell_new[k][l]=cell[i][l];
                              yadoblo[l]=true;
                              if(type[i][l]==0){
                                  cnttt++;
                                  totaltt += linktt[i][l];
                              }
                              nS[l].CarColor[k][l]=CarColor[i][l];
                              nS[l].CarDest[k][l] =CarDest[i][l];
                              nS[l].type_new[k][l]=type[i][l];
                              nS[l].CarOrig[k][l] =CarOrig[i][l];
                              nS[l].deptime[k][l] =deptime[i][l];
                              nS[l].Path[k][l] = Path[i][l];
                              nS[l].linktt[k][l] = 1;
                              if(nS[l].sname !=links[i][l]){
                                  links[i][l] = links[i][l]+"->"+nS[l].sname;
                              }
                              nS[l].links[k][l] =links[i][l];

                          //buses determine if they have to make a stop on the
                          //next segment
                              if (type[i][l]!=0){
                                  if (decideToStop(nS[l].vb.j)==true){
                                      nS[l].bus_stp[k][l]=-1;
                                  }
                              }
                              totdist[type[i][l]+1] += (j-i); 
                              if (dir != nS[l].dir){
                                  turncnt ++;
                              }else{
                                  noturncnt ++;
                              }
                          }
                      }else {
                             cell_new[i][l] = cell[i][l];
                              type_new[i][l] = type[i][l];
                      }
                  }
              }else if(cell[i][l] == 0){      
                  tottime[type[i][l]+1]++;
                  //if first car been waiting too long then just exit the 
                  //segment
                      cell_new[i][l] = cell[i][l];
                      type_new[i][l] = type[i][l];
                      linktt[i][l]++;
//                    }
              }
        }                 //yadoblo[l]=true;
      }
  }
    
    public void update_s() { 

    for (int i=0;i<LENGTH;i++){ 
       for(int l=0; l<LANES;l++){
           cell[i][l] = cell_new[i][l];   
           type[i][l] = type_new[i][l];
       }
    }

}
    
    public void update_news() { 
        for (int i=0;i<LENGTH;i++){ 
           for(int l=0; l<LANES;l++){
               cell_new[i][l]=-1;   
               type_new[i][l]=-1;
           }
        }
    
    }
      
    public boolean getBusStop(int i, int l, int cellsp){
        if (bus_stp[i][l]>0){
            bus_stp[i][l]--;
            return true;
        }else if (bus_stp[i][l]<=-1){
            //make sure the bus inside the bounds of the station
            if(i>=begin_station && i<=end_station && l == 0){
                //if bus is stopped, take the opportunity to stop
                //or if bus crosses the last cell of the station
                if(cellsp==0|| i+cellsp > end_station){
                    Random fRandom = new Random();
                    bus_stp[i][l]=(int)Math.round((double)Global.dwell + 
                            fRandom.nextGaussian()*(double)Global.dwell*
                                    Global.delta);
                    //bus_stp[i][l] = Global.dwell;
                    return true;
                }
            }
        }

        return false;    
    }
    
    public GraphPath getNewPath(Node orig,Node dest,GraphPath oldpath){
        //shortest path algorithm:
        StreetSegment nSS = null;
        GraphPath choosepath = null;
        
        double Beta = 0;
        if(orig!=dest){
            
            //calculate path probabilities
            if(orig.hashoflistsprobs.isEmpty() || 
                    !orig.hashoflistsprobs.containsKey(dest)){

                List<Double> probs = new ArrayList<Double>();               
                List<GraphPath> paths = CityApplet.ksd.odtopaths.get(orig.name)
                        .get(dest.name);
                List<Double> weights = new ArrayList<Double>();
                
                double sum = 0,tempw = 0;
                
                //get weights
                for (GraphPath path : paths) {
                    if(path == oldpath){
                        weights.add(999999.0);
                        continue;
                    }
                    tempw=0;
                    for (Object s:path.getEdgeList()){
                        StreetSegment st = (StreetSegment) s;
                        if(st == null){
                            System.out.println("here");
                        }
                        tempw += st.traveltime;
                        //tempw += st.LENGTH;
                    }
                    weights.add(tempw);
                }

                //get Beta
                double maxw=0, minw=99999999;
                for (double w  : weights) {
                    maxw = Math.max(maxw,w);
                    minw = Math.min(minw,w);
                }
                Beta = -Math.min(10/(maxw-minw),Double.MAX_VALUE);

                //get probabilities on each path
                for(double wone : weights){
                    double expsum = 0;
                    for(double wtwo: weights){
                        expsum += Math.exp(Beta*(wtwo-wone));
                    }
                    if (probs.size()==0){
                        probs.add(1/expsum); 
                    }else{
                        probs.add(1/expsum +probs.get(probs.size()-1)); 
                    }
                }
                probs.set(probs.size()-1,1.0);
                orig.hashoflistsprobs.put(dest,probs);
                orig.hashoflistspaths.put(dest,paths);
            }
            
            //get probs and paths
            List<Double> probs = orig.hashoflistsprobs.get(dest);
            List<GraphPath> paths = orig.hashoflistspaths.get(dest);
            //pick path
            double rand = Math.random();
            for (double prob : probs){
                if (rand < prob){
                    choosepath = paths.get(probs.indexOf(prob));
                    break;
                }
            }
        }
        return choosepath;
    }
    
    public StreetSegment getNextSegmentFromPath(GraphPath path, Node nd){
        StreetSegment nSS = null;
        if (path == null){
            return nSS;
        }

        for (Object s: path.getEdgeList()){
            StreetSegment st = (StreetSegment) s;
            if(st.va == nd){
                return st;
            }
        }
        
        return nSS;
    }
            
    public StreetSegment getNextBusSegmentRingRoad(int i, int l){
         for (StreetSegment ss : net.graph.outgoingEdgesOf(vb)){
                if(ss.dir == dir){
                    return ss;
                }
            }
        return null; 
    }
               
    public Node getRandomNode(Node dest){
        Node nd =dest;
        double min_dist=0;
        while (nd == dest && min_dist <= 5){
            //get random node
            int x = 0, y = 0;           
            x= (int)Math.floor(Math.random()*(Global.blocks));
            y = (int)Math.floor(Math.random()*(Global.blocks));
            nd = net.getNode(x,y);
            min_dist = getNodeDistanceRingNetwork(nd,dest);
        }
        return nd;
    }
    
    public int getNodeDistanceRingNetwork(Node a, Node b){
        int ax = a.j, ay = a.i, bx =b.j, by = b.i, mindist = 9999,dist=0;
        List <Integer> bxs = new ArrayList<Integer>();
        List <Integer> bys = new ArrayList<Integer>();
        int[] mult = {-1,0,1};
        //create 9 b points to simulate wrap around from ring network
        for (int i : mult){
            for (int j: mult){
                bxs.add(bx + i*Global.blocks);
                bys.add(by + j*Global.blocks);
            }
        }
        
        for(int k = 0; k<=bxs.size()-1;k++){
            dist = Math.abs(ax-bxs.get(k))+Math.abs(ay-bys.get(k));
            mindist = Math.min(dist,mindist);
        } 
        
        return mindist;
    }
    
    public double getSpeed() {
        double s=0;
        for (int i = 0; i < speed_ma.length; i++){
            s+=speed_ma[i];
        }
        if(s==0)s=0.00001;

        return s/speed_ma.length;
    }
    
    public double getLinkTravelTime(){
        double avgtt = 0;
        if (cnttt>0){
            avgtt=(double)totaltt/(double)cnttt;
        }else{
            if (getDens(0)>0.0){
                avgtt=(double)LENGTH/((double)net.Cost_dt*2.0) + (Global.cycle-
                        Global.green); //if cars are not moving assign value 
            }else{
                avgtt=(double)LENGTH/(double)Global.car_spd + Math.pow(Global.cycle-
                        Global.green,2)/(2*Global.cycle); // ff travel time + expected red
            }
        }
        if (CityApplet.t % resetdt*net.Cost_dt ==0){
            totaltt=0;cnttt=0;
        }
        return avgtt;
    }
    
    public double getLinkTravelTimeMovAv(){
        tt_ma[ttind] = getLinkTravelTime();
        ttind=(ttind+1)%ma_cnt;
        //find average 
        int sum = 0;
        for (double t : tt_ma) sum += t;
        traveltime = sum/tt_ma.length;
        return traveltime;
    }
    
    public void measure() {
        int vsum[]={0,0,0,0}, rhoc[]={0,0,0,0},effdenssum=0;
        
        for(int i=0;i<LENGTH;i++) {
           for (int l=0;l<LANES;l++){ 
                if (cell[i][l]>= 0) {
                      vsum[type[i][l]+1]+=cell[i][l];
                      rhoc[type[i][l]+1]++;
                     effdenssum++;
                }else{
                    if(i+1<LENGTH && type[i+1][l]==1 ||
                       i+2<LENGTH && type[i+2][l]==1) 
                        effdenssum++;
                }
           }
        }
        
        for (int i = 0; i <= 3; i++){
            speed[i]= ((rhoc[i] > 0) ? 
                    (double)(vsum[i])/(double)(rhoc[i]) 
                    : Global.car_spd);
            dens[i] = rhoc[i];
            flow[i] = speed[i]*dens[i];
            effdens = effdenssum;
        }
        speed_ma[n_ma] = speed[1];
        n_ma=(n_ma+1)%speed_dt;
  }
                     
    public boolean getVelBasedIncentive(int i,int targetl){
        int targetln_vel = 9999;
        if (i + gapcurr < LENGTH){
            for (int j = i;j <= i+lookahead;j++){
                if (j > LENGTH){
                    targetln_vel = 0; break;
                }
                if(cell[j][targetl]>-1) {
                    targetln_vel = j-i-1;
                    break;
                }
            }   
        }
        if (targetln_vel > lookahead)targetln_vel = 9999;
        if (gapcurr < targetln_vel){
            return true;
        }
        return false;
    }
       
    public void resetFlows(int i){
        outflow[i]= 0;
        totdist[i]= 0;
        tottime[i] =0;
        incnt[i] = 0;
    }
               
    public boolean decideToStop(int block){
        if (Global.nonrandomstop == false){
            if(Math.random()<Global.pstop){
                return true;
            }
        }
        else{
            double stopeveryn = Math.floor(1/Global.pstop);//stop every n blocks   
            if ((double)block%stopeveryn==0){
                return true;
            }
        }
        return false;
    }
          
    public Node getCarDest(int vehtype){
        Node dest = va; int x=0,y=0;
        if (vehtype ==0){ // if it is a car
                x = (int)Math.floor(Math.random()*(Global.blocks));
                y = (int)Math.floor(Math.random()*(Global.blocks));
                dest = net.getNode(x,y);
        }else{//it if is a bus
            dest=null;
        }
        return dest;
    }
    
    public int getFrontGap(int i, int l,int[][] cell,int[][] type, int length){
        int gap=9999;
        for (int j = i+1;j <= length;j++){
            if(cell[j][l]>-1) {
                if (type[j][l] == 0 || type [j][l] == 2)
                    gap = j-i-1;
                if (type[j][l] == 1)
                    gap = j-i-1-(Global.bus_lg-1);
                    if (gap<0)gap=0;
                if (gap < 0){
                    System.out.print("negative gap\n");System.exit(0);
                } 
                break;
            }
        }
        return gap;       
    }
    
    public double getDens(int i) {
        return dens[i];
    }
       
    public double getTravelProd(int i){
        return totdist[i];
    }   
    
}
