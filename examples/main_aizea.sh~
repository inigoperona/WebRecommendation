#!/bin/bash



## PARAMETERS ##
preprocess="01_preprocess"
mkdir -p $preprocess
database="02_DATABASE_5"
mkdir -p $database
validation="03_VALIDATION_5"
mkdir -p $validation






echo "## PREPROCESING LOGS ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A000MainClassPreprocess \
  $preprocess /LOGs_from_Jan9_toNov19.log
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A001MainClassCreateDatabase \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database





echo "## DISTANCE MATRIX ##"

# dist - sequence alignment
mkdir -p $database/DM_04_edit
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A012MainClassDistanceMatrixED \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database






echo "## EVALUATION BASED ON DATABASE: HOLD-OUT ##"

mkdir -p 03_VALIDATION
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A020MainClassHoldOut \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation





echo "## PAM ##"

for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A041MainClassPAM \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done




echo "## PAM+SPADE ##"

for dm in "DM_04_edit"
do
  pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A053MainClassPamSpadeKnnED \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done



