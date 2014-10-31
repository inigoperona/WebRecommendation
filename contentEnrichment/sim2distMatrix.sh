#!/bin/bash

cat ResSimilarity_koma.txt | tr ',' '.' > ResSimilarity.txt

cat ResSimilarity_koma.txt | tr ',' '.' | awk 'BEGIN{FS=";"}{
  printf "%f",(1-$1);
  for(i=2;i<=NF;i++){
    printf ";%f",(1-$i);
  }
  printf "\n"}' > DistantziaMatrizea.txt


