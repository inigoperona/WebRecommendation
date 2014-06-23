#!/bin/bash



## PARAMETERS ##
preprocess="01_preprocess"
mkdir -p $preprocess
database="02_DATABASE_5"
mkdir -p $database
validation="03_VALIDATION_5"
mkdir -p $validation






echo "## PREPROCESING LOGS ##"

mkdir -p $preprocess
../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A000MainClassPreprocess \
  $preprocess /LOGs_from_Jan9_toNov19.log
mkdir -p $database
../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A001MainClassCreateDatabase \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database





echo "## DISTANCE MATRIX ##"

# dist - sequence alignment
mkdir -p $database/DM_04_edit
../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A012MainClassDistanceMatrixED \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database






echo "## EVALUATION BASED ON DATABASE: CROSS VALIDATION ##"

mkdir -p $validation
../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A021MainClassCrossValidation \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation



### PROCESSING THE URLxTopicDistribution file ###

echo "## PREPROCESSING CONTENTS ##"

../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar ehupatras.webrecommendation.A100MainClassAddContent \
  $preprocess \
  $preprocess \
  "/document-topic-distributuions1_usageID.csv" \
  "/URLs_DM.txt" \
  "/URLs_to_topic.txt"





### HOLD-OUT ###

echo "## PAM ##"
for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A041MainClassPAMholdout \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done

echo "## PAM+SPADE ##"
for dm in "DM_04_edit"
do
  for top in "/URLs_to_topic.txt" "/URLs_to_topicModua0.txt" "/URLs_to_topicModua1.txt" "/URLs_to_topicModua2.txt"
  do
    # same topic score in results 0.5 
    pam="/pam_"$dm
    echo " "$pam"_spade_top05"
    ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop05 \
      $preprocess /LOGs_from_Jan9_toNov19.log $top \
      $database "/"$dm \
      $validation $pam

    # same topic score in results 1
    pam="/pam_"$dm
    echo " "$pam"_spade_top1"
    ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop1 \
      $preprocess /LOGs_from_Jan9_toNov19.log $top \
      $database "/"$dm \
      $validation $pam
  done
done






### CROSS-VALIDATION ###

echo "## PAM ##"
for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A041MainClassPAMcv \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done

echo "## PAM+SPADE ##"
for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnEDcv \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done

