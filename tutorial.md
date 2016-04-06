### CityApplet.java

Contains controls and main class
Can be used to set blocks, length, lanes, and nruns manually

##### initi()
creates the network, initializes visualization and sets default signalization values

##### run()
runs simulation. If loadnet==true, it will run one MFD run for each nruns

###DiaConv.java
Converts the units of the real world to pixel units for drawing purposes. 

###Diagram.java
Functionality for creating and drawing on a pane. 

###DiagramMFD.java
Extends Diagram class. Creates an MFD q-k pane and has tools to draw and update MFD diagram points.

###DiagramPane.java
Creates a diagram pane

###Global.java
Global variables 

###KShortestDistances.java
Finds shortest distance paths for the whole network and stores them in mindist

###Movie.java
Methods to create and update simulation visualization components

###Network.java
A network class which has tools to create and update the street network. 

#####update()

###Node.java
Includes street signal nodes and methods to update real-time signalization.

###StreetSegment.java
Street segment class to create and update each street segment

#####updateSpeeds()
saves the maximum gap for each vehicle based on the upstream vehicle. Save gaps to cell multidimensional array 

#####laneChange()
based on the current speed of the vehicles try to see if any vehicles can benefit from lane changing. Update vehicle to new cell on adjacent lane and get new gap.

#####updateSpeeds2()
get speeds by getting the minimum between maximum speed and gap 

#####updatePos()
update position of vehicles to cell_new multidimensional array. This new array serves as a temporary position tracker while updating the network. The main purpose here is to avoid vehicle conflicts where two vehicles might want to to end up on the same cell. This happens often when two vehicles from two different upstream segments try to enter the same downstream segment and want to reach the same cell.  

#####update_s()
Once cell_new has been updated and no vehicle conflicts are present, then it is copied to the original cell to update vehicles. 

#####updated_news()
Empty out cell_new so that there are no leftover vehicles for the next time step 

