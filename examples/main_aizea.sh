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






echo "## EVALUATION BASED ON DATABASE: HOLD-OUT ##"

mkdir -p $validation
../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A020MainClassHoldOut \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation





echo "## PAM ##"

for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A041MainClassPAM \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done




echo "## PAM+SPADE ##"

for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0/bin/java -Xmx2048m -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnED \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done



