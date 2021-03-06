
# convert topic-distribution file
# convert content-IDs to usage-IDs

while read line; do
  usageID=`echo $line | awk 'BEGIN{FS=","}{print $1}'`
  conteID=`echo $line | awk 'BEGIN{FS=","}{print $2}'`
  distline=`grep "^$conteID," topicTestuakBatera-document-topic-distributuionsTestuhutsa_contentID.csv`
  distline2=`echo $distline | awk 'BEGIN{FS=","}{print $2; for(i=3;i<=NF;i++){print ","$i}}'`
  echo $usageID","$distline2
done < usa2cont.csv > topicTestuakBatera-document-topic-distributuionsTestuhutsa_usageID_2.csv

