/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DirectedWeightedMultigraph;

/**
 *
 * @author fcastrillon3
 */
public class KShortestDistances {
    Map<String, Map<String, List<GraphPath>>> odtopaths =  
            new HashMap<String,Map<String, List<GraphPath>>>();
    static Map<String,Double> mindist = new HashMap<String,Double>();

    int kpaths = 4;
    DijkstraShortestPath dsp;
    FloydWarshallShortestPaths bfsp;
     
    public KShortestDistances(){
    }
    
    public void build(DirectedWeightedMultigraph<Node, StreetSegment> graph){
        odtopaths.clear();
        mindist.clear();
        System.out.println("getting shortest paths");
        DirectedWeightedMultigraph<Node, StreetSegment> tempgraph = 
                new DirectedWeightedMultigraph<Node, StreetSegment>(StreetSegment.class);
         
        for (Node nstart : graph.vertexSet()){
            Map<String,List<GraphPath>> tempmap = 
                            new HashMap<String, List<GraphPath>>();
            for (Node nend : graph.vertexSet()){
                Graphs.addAllEdges(tempgraph, graph, graph.edgeSet());
                List<GraphPath> templist = new ArrayList<GraphPath>();
                if (nstart != nend){
                    for (int i=0;i<kpaths;i++){
                        dsp = new DijkstraShortestPath(tempgraph,nstart,nend);
                        if(dsp.getPathEdgeList()==null) break;
                        if(i==0){
                            mindist.put(nstart.name+" to "+nend.name,
                                    dsp.getPathLength());
                        }
                        templist.add(dsp.getPath());
                        List<StreetSegment> temppath = 
                                new ArrayList<StreetSegment>(dsp.getPathEdgeList());
                        tempgraph.removeAllEdges(temppath);
                    }
                    tempmap.put(nend.name,templist);
                }
            }
            odtopaths.put(nstart.name,tempmap);
        }
        System.out.println("done getting shortest paths");
        tempgraph = null;
    }
    
    public void convertToNewGraph(DirectedWeightedMultigraph graph){
        System.out.println("updating paths");
        mindist.clear();
        //bfsp = new FloydWarshallShortestPaths(graph);
        Node nstart;
        Node nend;
        for (String nstart_nm: odtopaths.keySet()){
            for (String nend_nm: odtopaths.get(nstart_nm).keySet()){
                //get new nodes from new graph
                nstart = Network.nodenametost.get(nstart_nm);
                nend = Network.nodenametost.get(nend_nm);
                dsp = new DijkstraShortestPath(graph,nstart,nend);
                mindist.put(nstart.name+" to "+nend.name,
                        dsp.getPathLength());
                //get all paths between start and end
                for (GraphPath pathlist: odtopaths.get(nstart.name).get(nend.name)){
                    //change streets to new street names from new graph
                    for (Object sobj: pathlist.getEdgeList()){
                        StreetSegment old_st = (StreetSegment) sobj;
                        StreetSegment new_st = Network.streetnametost.get(old_st.sname);
                        if(new_st == null){
                            System.out.println("null");
                        }
                        pathlist.getEdgeList().set(
                                pathlist.getEdgeList().indexOf(old_st), new_st);
                        old_st = null;
                    }
                }
            }
        }
        System.out.println("done updating paths");
    }
          
    public double getMinDist(Node nstart, Node nend){
        return mindist.get(nstart.name + " to " + nend.name);
    }
        
}
