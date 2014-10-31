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
length(unique(cutree(hc, h=0.6))) # 36 clusters
length(unique(cutree(hc, h=0.7))) # 24 clusters
length(unique(cutree(hc, h=0.71))) # 22 clusters
length(unique(cutree(hc, h=0.72))) # 21 clusters
length(unique(cutree(hc, h=0.73))) # 18 clusters
length(unique(cutree(hc, h=0.74))) # 16 clusters
length(unique(cutree(hc, h=0.75))) # 16 clusters
length(unique(cutree(hc, h=0.76))) # 15 clusters ##
length(unique(cutree(hc, h=0.77))) # 15 clusters
length(unique(cutree(hc, h=0.78))) # 15 clusters
length(unique(cutree(hc, h=0.79))) # 15 clusters
length(unique(cutree(hc, h=0.8))) # 1 clusters
length(unique(cutree(hc, h=0.81))) # 1 clusters
length(unique(cutree(hc, h=0.82))) # 1 clusters
length(unique(cutree(hc, h=0.83))) # 1 clusters
length(unique(cutree(hc, h=0.84))) # 1 clusters
length(unique(cutree(hc, h=0.9))) # 1 clusters

# TestuHutsa
load("DendrogramTestuHutsa.RData")
length(unique(cutree(hc, h=0.6))) # 36 clusters
length(unique(cutree(hc, h=0.7))) # 21 clusters
length(unique(cutree(hc, h=0.71))) # 19 clusters
length(unique(cutree(hc, h=0.72))) # 16 clusters
length(unique(cutree(hc, h=0.73))) # 16 clusters
length(unique(cutree(hc, h=0.74))) # 15 clusters
length(unique(cutree(hc, h=0.75))) # 13 clusters
length(unique(cutree(hc, h=0.76))) # 13 clusters ##
length(unique(cutree(hc, h=0.77))) # 12 clusters
length(unique(cutree(hc, h=0.78))) # 12 clusters
length(unique(cutree(hc, h=0.79))) # 12 clusters
length(unique(cutree(hc, h=0.8))) # 1 clusters
length(unique(cutree(hc, h=0.81))) # 1 clusters
length(unique(cutree(hc, h=0.82))) # 1 clusters
length(unique(cutree(hc, h=0.83))) # 1 clusters
length(unique(cutree(hc, h=0.84))) # 1 clusters
length(unique(cutree(hc, h=0.9))) # 1 clusters



# create cluster partitions using h=0.82
cutH = 0.76
for(mod in c("Modua2", "TestuHutsa")){
  denF = paste("Dendrogram", mod, ".RData", sep="")
  load(denF)
  partition = cutree(hc, h=cutH)
  names(partition) = 1:length(partition)
  partF = paste("ClusterPartition", mod, ".txt", sep="")
  write.table(partition, file=partF, quote=FALSE, sep=";", row.names=TRUE, col.names=FALSE)
}

