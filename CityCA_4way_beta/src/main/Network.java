package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.DirectedWeightedMultigraph;


public class Network {
    public int BLOCKS, TurnProbOption,LANES, NLINKS, maxlen;
    public double dens, flow, speed, travelprod,inflow,traveltime,busprop, 
            outflow,Pmax=0, GenerationRate=0,  CompletionRate=0;
    public double dens0, flow0, speed0,tempdens0, effdens;
    public double dens1, flow1, speed1,tempdens1,densWE,densEW,densNS,densSN,
            densbusWE,densbusEW,densbusNS,densbusSN,denscorr;
    public double outflow0, outflow1,inflow0,inflow1,flowWE,flowEW,flowNS,flowSN,
            travelprod0,travelprod1,traveltime0,traveltime1,travelprodWE,
           travelprodEW,travelprodNS,travelprodSN,travelprodbusWE,
           travelprodbusEW,travelprodbusNS,travelprodbusSN,travelprodcorr;
    public boolean incident=false,blength=false;
    public int ns1=0,ns2=0,ns3=0,ns4=0,Cost_dt = 1,kpaths=4;
    private int cnt[] = {0,0,0};
    public int count1=0,count2=0,count3=0;
    double turncnt;
    double noturncnt;
    private int blocklen=0;
    public  DirectedWeightedMultigraph<Node, StreetSegment> graph; // The graph of the network
    public static Map<String,StreetSegment> SShash = new HashMap<String,StreetSegment>();
    private static Map<Integer,StreetSegment> netlenhash = new HashMap<Integer,StreetSegment>();
    private static List<Integer> blwidth = new ArrayList<Integer>();
    private static List<Integer> blheight = new ArrayList<Integer>();
    BellmanFordShortestPath sp;
    DijkstraShortestPath dsp;
    KShortestPaths ksp;
    int mb = 1024*1024;
    Runtime runtime = Runtime.getRuntime();
    public static Map <String,StreetSegment> streetnametost = 
            new HashMap<String,StreetSegment>();
    public static Map <String,Node> nodenametost = 
            new HashMap<String,Node>();
    
    public Network(int b, int L, int op, int ln) {
        Node va=null, vb=null, vfirst=null; int n_inters;
        BLOCKS=b;
        NLINKS = 2*b*(b+1);
        LANES = ln;
        TurnProbOption=op;
        StreetSegment st;
        graph = new DirectedWeightedMultigraph<Node, StreetSegment>(StreetSegment.class);
        blwidth.clear();
        blheight.clear();
        blwidth.add(0);
        blheight.add(0);
        StreetSegment.kveh = Math.random();
        
        // get block lengths and widths
        for (int i = 0; i < BLOCKS; i++){
            blocklen = Math.max(1,
                                (int)Math.round(getGaussian(L,Global.delta)));
            blwidth.add(blwidth.get(blwidth.size()-1)+blocklen);
            blocklen = Math.max(1,
                                (int)Math.round(getGaussian(L,Global.delta)));
            blheight.add(blheight.get(blheight.size()-1)+blocklen);
        }

        //create horizontal links
        n_inters = BLOCKS;
        for (int i = 0; i < n_inters; i++){
            for (int j = 0; j < BLOCKS+1; j++){

                if(j==0){
                    vfirst = new Node(i, j);
                    va = vfirst;
                    graph.addVertex(va);    
                    nodenametost.put(va.name,va);
                }else{
                    if(j==BLOCKS){
                        vb = vfirst;
                    }else{
                        vb = new Node(i, j);
                        graph.addVertex(vb); 
                        nodenametost.put(vb.name,vb);
                    }        
                    int ind = (vb.j > 0) ? vb.j : BLOCKS;
                    st = new StreetSegment(this, va, vb, 
                            blwidth.get(ind)-blwidth.get(ind-1), 
                            Global.WE,LANES);
                    streetnametost.put(st.sname,st);
                    graph.addEdge(va, vb, st);
                    graph.setEdgeWeight(graph.getEdge(va,vb), 1);
                    st = new StreetSegment(this, vb, va,
                            blwidth.get(ind)-blwidth.get(ind-1), 
                            Global.EW,LANES);
                    streetnametost.put(st.sname,st);
                    graph.addEdge(vb, va, st);
                    graph.setEdgeWeight(graph.getEdge(vb,va), 1);
                    va = vb;
                }

            }
        }
        
        //create vertical links
        for (int j = 0; j < n_inters; j++){
            for (int i = 0; i < BLOCKS+1; i++){
                if(i==0){
                    vfirst = getNode(i,j);
                    va = vfirst;
                }else{
                    if(i==BLOCKS){
                        vb = vfirst;
                    }else{
                        vb = getNode(i, j);
                    }   

                    int ind = (vb.i > 0) ? vb.i : BLOCKS;
                    ;

                    st = new StreetSegment(this, va, vb, 
                            blheight.get(ind)-blheight.get(ind-1), 
                            Global.NS,LANES);
                    streetnametost.put(st.sname,st);
                    graph.addEdge(va, vb, st);
                    graph.setEdgeWeight(graph.getEdge(va,vb),1);
                    st = new StreetSegment(this, vb, va, 
                            blheight.get(ind)-blheight.get(ind-1), 
                            Global.SN,LANES);
                    streetnametost.put(st.sname,st);
                    graph.addEdge(vb, va, st);
                    graph.setEdgeWeight(graph.getEdge(vb,va),1);
                     //}access
                    va = vb;
                }
            }
        }
  
        // initialize streets and create logic for connecting links 
        for(StreetSegment s : graph.edgeSet()){
            s.ini();
            va = graph.getEdgeSource(s);
            vb = graph.getEdgeTarget(s);
            s.nOutLinks = graph.outDegreeOf(vb);
            for(StreetSegment sT : graph.outgoingEdgesOf(vb)){
                Node vbT = graph.getEdgeTarget(sT);
                if(va.i == vb.i){//E-W link
                    if(((va.j==0)||(va.j==BLOCKS)))
                        s.access=true;
                    if((vbT.i == va.i)&&(vbT.j!=va.j))s.nextS=sT;
                    
                }else{
                    if(((va.i==0)||(va.i==BLOCKS)))
                        s.access=true;
                    //if((va.j==0)&&(va.j==0))

                    if((vbT.j == va.j)&&(vbT.i!=va.i))s.nextS=sT;
                }
            }
        }
        
        //initial vehicle positions
        maxlen = Math.max(0,getMaxNetLen("width"));
        
        //map street segments with names
        int l = 0;
        for(StreetSegment s : graph.edgeSet()){
            SShash.put(s.sname,s);
            netlenhash.put(l,s);
            l += s.LENGTH;
        }
        update_cost();
        if (CityApplet.t>=0){
            getShortestDistance(3);
        }
        
    }
    
    public void ini(){
        
    }
        
    public void update(){
        if (CityApplet.t%Cost_dt==0)update_cost();
        //for(StreetSegment s : graph.edgeSet())s.printcells();
        //int coonter=0,coonter2=0;
        
        //int cnt1 = calculate_cnt();
        for(StreetSegment s : graph.edgeSet()){
            //if (s.va.i == 0 && s.dir == 2)
            s.boundaries(busprop,blength);
        }
        for(StreetSegment s : graph.edgeSet())s.updateSpeeds();
        if (Global.lanes > 1){
            for(StreetSegment s : graph.edgeSet())s.laneChange();}
        for(StreetSegment s : graph.edgeSet())s.updateSpeeds2();
        for(StreetSegment s : graph.edgeSet())s.updatePos();
        for(StreetSegment s : graph.edgeSet())s.update_s();
        for(StreetSegment s : graph.edgeSet())s.update_news();      
        measure();
        
    }

    public void measure(){
        tempdens0 = 0;
        tempdens1 = 0;
        count1 = count2 = count3 = 0;
        for(StreetSegment s : graph.edgeSet()){
                s.measure();
                tempdens0+= s.getDens(1);
                tempdens1 += Math.max(s.getDens(2),s.getDens(3));
                dens0+= s.getDens(1);
                dens1+= Math.max(s.getDens(2),s.getDens(3));            
                travelprod0 += s.getTravelProd(1);
                travelprod1 += Math.max(s.getTravelProd(2),s.getTravelProd(3));
                count1 += s.cnt1;
                count2 +=s.cnt2;
                count3+= s.cnt3;
                }
//        }
        busprop = tempdens1/(tempdens0+tempdens1); 
    }

    public void restartflows(){
        dens0 = outflow0 = outflow1 = traveltime0 =traveltime1=inflow0 = inflow1 = 0;
    }
    
    public void calc_nst(){
        for(StreetSegment s : graph.edgeSet()){
            ns1+= (s.dir==Global.WE)? 1:0;
            ns2+= (s.dir==Global.EW)? 1:0;
            ns3+= (s.dir==Global.NS)? 1:0;
            ns4+= (s.dir==Global.SN)? 1:0;
       }  
    }

    public int getNumLinks(int dir){
        if (dir==1) return ns1;
        if (dir==2) return ns2;
        if (dir==3) return ns3;
        if (dir==4) return ns4;
        else return ns1+ns2+ns3+ns4;
    }
        
    public Node getNode(int i, int j) {
        Node v = null;
        for(Node n : graph.vertexSet())
            if((n.i == i)&&(n.j == j)){
                v = n;
                break;
            }
        return v;
    }
    
    public void update_stopcnt(int stopcnt){
        int cnt= 0;
        
        //System.out.print("-------------------------\n");
        for(StreetSegment s : graph.edgeSet()){
            for(int l=0; l<s.LANES;l++){
                s.stopcnt[l] = stopcnt;
            }
        }
    }
    
    private void update_cost() {
        double cost=0;
        for(StreetSegment s : graph.edgeSet()){
            s.getLinkTravelTimeMovAv();
            cost = s.LENGTH;
            graph.setEdgeWeight(graph.getEdge(s.va,s.vb),cost);
            
        }
        for(Node v : graph.vertexSet()) {
            v.hashoflistspaths.clear();
            v.hashoflistsprobs.clear();
        }

    }
    
    public void restartnet(){
        resetqkv();
        resetcnts();
        for(StreetSegment s : graph.edgeSet()){
            for (int i = 0; i < s.LENGTH; i++){
                for (int j = 0; j < s. LANES; j++){
                    s.cell[i][j] = s.cell_new[i][j] = -1;
                    s.type[i][j] = s.type_new[i][j] = -1;
                    s.va.resetTiming();
                    s.vb.resetTiming();
                }
            }
        }
    }
    
   private void getShortestDistance(int nettype){
        int nnodes = (nettype == 3) ? (int) Math.pow(Global.blocks,2):
                (int) Math.pow(Global.blocks+1,2);
        if (CityApplet.ksd.odtopaths.size()!= nnodes){
            CityApplet.ksd.build(graph);
        }else{
            CityApplet.ksd.convertToNewGraph(graph);
        }
    }

   private double getGaussian(double aMean, double aCov){
        Random fRandom = new Random();
        return aMean + fRandom.nextGaussian()*aMean*aCov;
    }

    private int getMaxNetLen(String horw){
        if(horw.matches("height")){
            return  (blheight.get(blheight.size()-1));
        }else if(horw.matches("width")){
            return  (blwidth.get(blwidth.size()-1));
        }
        return (0);
    }
   
    public List<Integer> getBlockHeights(){
        return blheight;
    }
    
    public List<Integer> getBlockWidths(){
        return blwidth;
    }
    
    public Integer getMaxHeight(){
        return (blheight.get(blheight.size()-1));
    }
    
    public Integer getMaxWidth(){
        return (blwidth.get(blwidth.size()-1));
    }

    public double getNetworkLen(){
        double len=0;
        for(StreetSegment st : graph.edgeSet()){
            len += st.LENGTH;
        }
        return len;
    }
              
    public double getArea(int code){
        double area=0;
        for (StreetSegment s : graph.edgeSet()){
            if (s.dir == code){
                area += s.LENGTH*LANES;
            }
        }
        return area;
    }
    
    public void setRndCycles(double u){
        for(Node v : graph.vertexSet())
            v.rndCycle= u*Math.random();
    }

    public void setSignalTiming(){
        for(Node v : graph.vertexSet())v.setSignalTiming(CityApplet.t);
    }
    
    public void resetTiming(){
        for(Node v : graph.vertexSet())v.resetTiming();
    }
    
    public void resetqkv(){
        dens0=0; flow0=0; speed0=0;
        dens1=0; flow1=0; speed1=0;
        densEW=0;flowEW=0;densNS=0;densSN=0; denscorr=0;
        densWE=0;flowWE=0;flowNS=0;flowSN=0;densbusEW=0;densbusWE=0;densbusNS=0;
        densbusSN=0;
        outflow0=0; outflow1=0;
        inflow0=0; inflow1=0;
        travelprod0=0; travelprod1=0;travelprodEW=0;travelprodWE=0;
        travelprodNS=0;travelprodSN=0;travelprodbusEW=0;travelprodbusWE=0;
        travelprodbusNS=0;travelprodbusSN=0;
        traveltime0=0; traveltime1=0;travelprodcorr=0;
        effdens=0;
    }
    
    public void resetcnts(){
        for(StreetSegment s : graph.edgeSet()){
            s.resetFlows(0);
            s.resetFlows(1);
            s.resetFlows(2);
            s.resetFlows(3);
        }
    }
    
    public void resetHeadways(){
        for(StreetSegment s : graph.edgeSet()){
            for (int l=0;l<LANES;l++){
                s.nextbust[l] = 0;
                s.override[l]=false;
            }
        }
    }
    
    public double getDens() {
        dens=(dens0+dens1);
        //System.out.print("dens "+dens+"\n");
        return dens;
    }
       
    public double getTravelProd(){
        travelprod = travelprod0 + travelprod1;
        return travelprod;
    }

}
