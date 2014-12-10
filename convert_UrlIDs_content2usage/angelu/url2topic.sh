#!/bin/bash


for mod in "TestuHutsa" "Modua0" "Modua1" "Modua2"
do

while read line; do
  usageID=`echo $line | awk '{print $1}'` 
  topicID=`echo $line | awk '{print $2}'` 
  convLine=`grep "^$usageID," ../usa2cont.csv`
  contID=`echo $convLine | awk 'BEGIN{FS=","}{print $2+1}'`
  echo $contID";"$topicID
done < URL2topic_th0_usageID/URLs_to_topic_${mod}_th0_usageID.txt | sort -n > URL2topic_th0_contID/URLs_to_topic_${mod}_th0_contID.txt

done

