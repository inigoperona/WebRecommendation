#!/usr/bin/Rscript



# create dendrograms from URLxURL distance matrixes
for(mod in c("Modua2", "TestuHutsa")){
  f = paste("../Resultados", mod, "/DistantziaMatrizea.txt", sep="")
  data = read.table(file=f, sep=";")
  #data2 = data[,1:length(data)-1]
  m = as.matrix(data)
  dm = as.dist(m, diag=TRUE, upper=TRUE)
  hc = hclust(dm)
  sf = paste("Dendrogram", mod, ".RData", sep="")
  save(hc, file=sf)
}



# find the most suitable height to cut the dendrogram

# Modua2
load("DendrogramModua2.RData")
length(unique(cutree(hc, h=0.6))) # 84 clusters
length(unique(cutree(hc, h=0.7))) # 37 clusters
length(unique(cutree(hc, h=0.8))) # 13 clusters
length(unique(cutree(hc, h=0.81))) # 10 clusters
length(unique(cutree(hc, h=0.82))) # 10 clusters ##
length(unique(cutree(hc, h=0.83))) # 10 clusters
length(unique(cutree(hc, h=0.84))) # 1 clusters
length(unique(cutree(hc, h=0.9))) # 1 clusters

# TestuHutsa
load("DendrogramTestuHutsa.RData")
length(unique(cutree(hc, h=0.6))) # 112 clusters
length(unique(cutree(hc, h=0.7))) # 52 clusters
length(unique(cutree(hc, h=0.8))) # 20 clusters
length(unique(cutree(hc, h=0.81))) # 15 clusters
length(unique(cutree(hc, h=0.82))) # 11 clusters ##
length(unique(cutree(hc, h=0.83))) # 11 clusters
length(unique(cutree(hc, h=0.84))) # 1 clusters
length(unique(cutree(hc, h=0.9))) # 1 clusters



# create cluster partitions using h=0.82
cutH = 0.82
for(mod in c("Modua2", "TestuHutsa")){
  denF = paste("Dendrogram", mod, ".RData", sep="")
  load(denF)
  partition = cutree(hc, h=cutH)
  names(partition) = 1:length(partition)
  partF = paste("ClusterPartition", mod, ".txt", sep="")
  write.table(partition, file=partF, quote=FALSE, sep=";", row.names=TRUE, col.names=FALSE)
}

