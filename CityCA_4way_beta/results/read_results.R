setwd("C:/Users/Castri/Dropbox/Research/Thesis/CityCA_4way/CityCA_4way_OD/results")
file=read.csv("results.txt",sep=",")

density = file[,1]
density2 = file[,2]
flow = (file[,3])
outflow = (file[,4])
travprod = (file[,5])
inflow = (file[,6])

plot(flow,outflow)
plot(flow,travprod)
plot(inflow,travprod)
plot(density,flow)
plot(density,outflow)
plot(density,travprod)
plot(density,density2)
plot(inflow,outflow)
points(inflow-outflow,col='blue')
plot(density)
plot(inflow)abline