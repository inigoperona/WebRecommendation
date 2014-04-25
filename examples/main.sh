#!/bin/bash



## PARAMETERS ##
preprocess="01_preprocess"
mkdir -p $preprocess
database="02_DATABASE_5"
mkdir -p $database
validation="03_VALIDATION_5"
mkdir -p $validation






echo "## PREPROCESS ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A000MainClassPreprocess \
  $preprocess /LOGs_from_Jan9_toNov19.log
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A001MainClassCreateDatabase \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database







echo "## DISTANCE MATRIX ##"

## Raw sequences ##

# data - sequence alignment
#mkdir -p $database/DM_00_no_role_data
#mkdir -p $database/DM_03_intelligent2_data
#../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A010MainClassDistanceMatrixEuclidean \
#  $preprocess /LOGs_from_Jan9_toNov19.log \
#  $database

# dist - sequence alignment
mkdir -p $database/DM_00_no_role_dist
mkdir -p $database/DM_03_intelligent2_dist
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A011MainClassDistanceMatrixInverse \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_04_edit
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A012MainClassDistanceMatrixED \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database

# dist - normalized compress distance (NCD)
mkdir -p $database/DM_05_ncd_bzip2
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar;commons-compress-1.8.jar ehupatras.webrecommendation.A013MainClassDistanceMatrixNcdBzip2 \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_05_ncd_gzip
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar;commons-compress-1.8.jar ehupatras.webrecommendation.A014MainClassDistanceMatrixNcdGzip \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database

# dist - split sequences by index - sequence alignment
mkdir -p $database/DM00-no_role-split
mkdir -p $database/DM03-U_HC2-split
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A011MainClassDistanceMatrixInverseSplit \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM04-edit-split
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A012MainClassDistanceMatrixEDSplit \
  preprocess /LOGs_from_Jan9_toNov19.log \
  $database

## Introducing topics ##

# URL to URL distance by euclidean distance between topic-histograms (continue value)
# it expects the file > preprocessingWD + "/URLs_DM.txt"
mkdir -p $database/DM_00_no_role_dist_topics
mkdir -p $database/DM_03_intelligent2_dist_topics
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A111MainClassDistanceMatrixInverseTopics \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_04_edit_dist_topics
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A112MainClassDistanceMatrixEDTopics \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database

# Two URLs are the same (0); are from the same topic (0.5); or are different (1) (discrete values)
# it expects the file > preprocessingWD + "/URLs_to_topic.txt"
mkdir -p $database/DM_00_no_role_dist_topics2
mkdir -p $database/DM_03_intelligent2_dist_topics2
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A113MainClassDistanceMatrixInverseTopics2 \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_04_edit_dist_topics2
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A114MainClassDistanceMatrixEDTopics2 \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database






echo "## EVALUATION BASED ON DATABASE: HOLD-OUT ##"

mkdir -p 03_VALIDATION
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A020MainClassHoldOut \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation






echo "## MARKOV CHAIN ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A030MainClassMarkovChain \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database /DM_00_no_role_dist \
  $validation
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A030MainClassMarkovChainSplit \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database /DM00-no_role-split \
  $validation


echo "## GLOBAL SUFFIX TREE ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A031MainClassSuffixTree \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database /DM_00_no_role_dist \
  $validation
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A031MainClassSuffixTreeSplit \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database /DM00-no_role-split \
  $validation







echo "## Hclust ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  hclust="/hclust_"$dm
  echo " "$hclust
  mkdir -p $validation""$hclust
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A040MainClassHclust \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  hclust="/hclust_"$dm
  echo " "$hclust
  mkdir -p $validation""$hclust
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A040MainClassHclustSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done



echo "## PAM ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A041MainClassPAM \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A041MainClassPAMSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done






echo "## HCLUST+MSA+WSEQ+ST ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  hclust="/hclust_"$dm
  cmsast=$hclust"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A050MainClassHclustMsaSt \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust $cmsast
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  hclust="/hclust_"$dm
  cmsast=$hclust"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A050MainClassHclustMsaStSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust $cmsast
done



echo "## PAM+MSA+WSEQ+ST ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  pam="/pam_"$dm
  cmsast=$pam"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A051MainClassPAMMsaSt \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam $cmsast
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  pam="/pam_"$dm
  cmsast=$pam"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A051MainClassPAMMsaStSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam $cmsast
done






echo "## HCLUST+SPADE ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A052MainClassHclustSpade \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A052MainClassHclustSpadeSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done



echo "## PAM+SPADE ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A053MainClassPamSpade \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
    pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A053MainClassPamSpadeSplit \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done







echo "## Hclust+HMM ##"

for dm in "DM_04_edit"
do
  hclust="/hclust_"$dm
  hmm=$hclust"/hmm"
  echo " "$hmm
  mkdir -p $validation""$hmm
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A060MainClassHclustHMM \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust $hmm
done







echo "### Hclust+ST+fit (Modular approach 1) ###"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_modular1"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A054MainClassHclustST \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_modular1"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.KKKKKKKKKKKKKKKKK \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done

echo "### Hclust+ST+knn (Modular approach 2) ###"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_modular2"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.A055MainClassHclustST2 \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done
for dm in "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_modular2"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco.jar ehupatras.webrecommendation.KKKKKKKKKKKKKKKKKKKK \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done


