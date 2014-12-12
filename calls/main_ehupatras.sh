#!/bin/bash


## PARAMETERS ##
parameters="-"

preprocess="01_preprocess"
database="02_DATABASE_5"
validation="03_VALIDATION_5"
mkdir -p $preprocess
mkdir -p $database
mkdir -p $validation

dm="DM_04_edit"
stra=0
clust="pam_"$dm

function changeParameters {
top=$1
mod=$2
log=$3
wseq=$4

# content files
declare -a sMatrixA=( \
"$preprocess/Content/Ont3_025_060/Resultados${mod}/ResSimilarity.txt" \
"$preprocess/Content/Topic/similarityHellingerTopic1${mod}.txt")
declare -a rMatrixA=( \
"$preprocess/Content/Ont3_025_060/Resultados${mod}/ResRelations.txt" \
"$preprocess/Content/Topic/relationMatrixTopic1${mod}.txt")
declare -a urlClu=(\
"$preprocess/Content/Ont3_025_060/clusterPartitions/ClusterPartition${mod}.txt" \
"$preprocess/Content/Topic/URLs_to_topic_th0/URLs_to_topic_${mod}_th0_contID_cl.txt")

parameters="$preprocess "\
"/BidasoaTurismo_LOGs_from_Jan9_toNov19.log "\
"/Content/Topic/URLs_to_topic_th0/URLs_to_topic_${mod}_th0_usageID.txt "\
"/Content/Topic/DM_similarityHellingerTopic1${mod}_usageID.txt "\
"${sMatrixA[${top}]} ${rMatrixA[${top}]} ${urlClu[${top}]} "\
"$preprocess/Content/usa2cont.csv "\
"$database "\
"/$dm "\
"$validation "\
"/$clust "\
"/$wseq "\
"/$log "\
"- "\
"0"

echo "parameters: "$parameters
}



# PREPROCESSING
changeParameters 1 "TestuHutsa" "-" "-"


echo "## PREPROCESING LOGS ##"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A000MainClassPreprocess $parameters > preprocessing.txt
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A001MainClassCreateDatabase $parameters > createDB.txt



echo "## DISTANCE MATRIX ##"
# URL abstraction
mkdir -p $database/DM_00_norole_dist
mkdir -p $database/DM_04_similarHC2_dist
changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A011MainClassDistanceMatrixInverse $parameters
mkdir -p $database/DM_04_edit
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A012MainClassDistanceMatrixED $parameters
mkdir -p $database/DM_05_ncd_bzip2
../jre1.7.0_71/bin/java -Xmx2048m -cp "webreco.jar:commons-compress-1.8.jar" \
  ehupatras.webrecommendation.A013MainClassDistanceMatrixNcdBzip2 $parameters
mkdir -p $database/DM_05_ncd_gzip
../jre1.7.0_71/bin/java -Xmx2048m -cp "webreco.jar:commons-compress-1.8.jar" \
  ehupatras.webrecommendation.A014MainClassDistanceMatrixNcdGzip $parameters
# Topic abstraction 1: Continue
mkdir -p $database/DM_00_norole_dist_TopicCont
mkdir -p $database/DM_04_similarHC2_dist_TopicCont
changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A111MainClassDistanceMatrixInverseTopics $parameters
mkdir -p $database/DM_04_edit_TopicCont
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A112MainClassDistanceMatrixEDTopics $parameters
# Topic abstraction 2: Discrete
mkdir -p $database/DM_00_norole_dist_topicsDisc
mkdir -p $database/DM_04_similarHC2_dist_topicsDisc
changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A113MainClassDistanceMatrixInverseTopics2 $parameters
mkdir -p $database/DM_04_edit_topicsDisc
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A114MainClassDistanceMatrixEDTopics2 $parameters



echo "## EVALUATION BASED ON DATABASE: CROSS VALIDATION ##"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar ehupatras.webrecommendation.A020MainClassHoldOut $parameters



echo "## Benchmark ##"
ben="benchmark"
mkdir -p $validation/$ben
changeParameters 1 "TestuHutsa" "$ben/evalTrack_mc.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A030MainClassMarkovChain \
  $parameters > 000_mc.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstR.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0310MainClassSuffixTreeGoToRoot \
  $parameters > 001_gstR.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstLS.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0311MainClassSuffixTreeGoToLongestSuffix \
  $parameters > 001_gstLS.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstLP.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0312MainClassSuffixTreeGoToLongestPrefix \
  $parameters > 001_gstLP.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstL1S.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0313MainClassSuffixTreeGoToLength1Suffix \
  $parameters > 001_gstL1S.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstLSL1.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1Suffix \
  $parameters > 001_gstLSL1.txt
changeParameters 1 "TestuHutsa" "$ben/evalTrack_gstLSL1N1.txt" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1SuffixNorm1 \
  $parameters > 001_gstLSL1N1.txt



## Clustering
echo "## Clustering ##"
for dm in "DM_00_norole_dist" "DM_04_similarHC2_dist" "DM_04_edit" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  clust="/hclust_"$dm
  mkdir -p $validation/$clust
  changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A040MainClassHclust $parameters
done > clust_hclust.txt
for dm in "DM_00_norole_dist" "DM_04_similarHC2_dist" "DM_04_edit" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  clust="/pam_"$dm
  mkdir -p $validation/$clust
  changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A041MainClassPAM $parameters
done > clust_pam.txt
for dm in "DM_00_norole_dist_TopicCont" "DM_04_similarHC2_dist_TopicCont" "DM_04_edit_TopicCont" "DM_00_norole_dist_topicsDisc" "DM_04_similarHC2_dist_topicsDisc" "DM_04_edit_topicsDisc"
do
  clust="/hclust_"$dm
  mkdir -p $validation/$clust
  changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A040MainClassHclust $parameters
done > clust_hclust_topic.txt
for dm in "DM_00_norole_dist_TopicCont" "DM_04_similarHC2_dist_TopicCont" "DM_04_edit_TopicCont" "DM_00_norole_dist_topicsDisc" "DM_04_similarHC2_dist_topicsDisc" "DM_04_edit_topicsDisc"
do
  clust="/pam_"$dm
  mkdir -p $validation/$clust
  changeParameters 1 "TestuHutsa" "-" "-"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A041MainClassPAM $parameters
done > clust_pam_topic.txt



## clust+MSA+Wseq+ST
echo "## clust+MSA+Wseq+ST"
for dm in "DM_00_norole_dist" "DM_04_similarHC2_dist" "DM_04_edit" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  clust="hclust_"$dm
  mkdir -p "$validation/$clust/wseq"
  changeParameters 1 "TestuHutsa" "$clust/wseq/evalTrack_hclust-MSA-Wseq-ST.txt" "$clust/wseq"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0500MainClassHclustMsaSt $parameters
  mkdir -p "$validation/$clust/wseq_norm1"
  changeParameters 1 "TestuHutsa" "$clust/wseq_norm1/evalTrack_hclust-MSA-Wseq-ST-Norm1.txt" "$clust/wseq_norm1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0500MainClassHclustMsaStNorm1 $parameters
  mkdir -p "$validation/$clust/wseq_spade1"
  changeParameters 1 "TestuHutsa" "$clust/wseq_spade1/evalTrack_hclust-SPADE1-ST.txt" "$clust/wseq_spade1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0501MainClassHclustSpadeSt $parameters

  clust="pam_"$dm
  mkdir -p "$validation/$clust/spade1"
  changeParameters 1 "TestuHutsa" "$clust/spade1/evalTrack_pam-MSA-Wseq-ST.txt" "$clust/spade1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A051MainClassPAMMsaSt $parameters
done > clust_wseq_st.txt
for dm in "DM_00_norole_dist_TopicCont" "DM_04_similarHC2_dist_TopicCont" "DM_04_edit_TopicCont" "DM_00_norole_dist_topicsDisc" "DM_04_similarHC2_dist_topicsDisc" "DM_04_edit_topicsDisc"
do
  clust="hclust_"$dm
  mkdir -p "$validation/$clust/wseq"
  changeParameters 1 "TestuHutsa" "$clust/wseq/evalTrack_hclust-MSA-Wseq-ST.txt" "$clust/wseq"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0500MainClassHclustMsaSt $parameters
  mkdir -p "$validation/$clust/wseq_norm1"
  changeParameters 1 "TestuHutsa" "$clust/wseq_norm1/evalTrack_hclust-MSA-Wseq-ST-Norm1.txt" "$clust/wseq_norm1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0500MainClassHclustMsaStNorm1 $parameters
  mkdir -p "$validation/$clust/wseq_spade1"
  changeParameters 1 "TestuHutsa" "$clust/wseq_spade1/evalTrack_hclust-SPADE1-ST.txt" "$clust/wseq_spade1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A0501MainClassHclustSpadeSt $parameters

  clust="pam_"$dm
  mkdir -p "$validation/$clust/spade1"
  changeParameters 1 "TestuHutsa" "$clust/spade1/evalTrack_pam-MSA-Wseq-ST.txt" "$clust/spade1"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A051MainClassPAMMsaSt $parameters
done > clust_wseq_st_topic.txt



## clust+SPADE+KnnED
echo "## clust+SPADE+KnnED"
for dm in "DM_00_norole_dist" "DM_04_similarHC2_dist" "DM_04_edit" "DM_05_ncd_bzip2" "DM_05_ncd_gzip"
do
  clust="hclust_"$dm
  mkdir -p "$validation/$clust/spade1_knnED"
  changeParameters 1 "TestuHutsa" "$clust/spade1_knnED/evalTrack_hclust-SPADE-knnED.txt" "$clust/spade1_knnED"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A052MainClassHclustSpadeKnnED $parameters

  clust="pam_"$dm
  mkdir -p "$validation/$clust/spade1_knnED"
  changeParameters 1 "TestuHutsa" "$clust/spade1_knnED/evalTrack_pam-SPADE-knnED.txt" "$clust/spade1_knnED"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A053MainClassPamSpadeKnnED $parameters
done > clust_spade_knnED.txt
for dm in "DM_00_norole_dist_TopicCont" "DM_04_similarHC2_dist_TopicCont" "DM_04_edit_TopicCont" "DM_00_norole_dist_topicsDisc" "DM_04_similarHC2_dist_topicsDisc" "DM_04_edit_topicsDisc"
do
  clust="hclust_"$dm
  mkdir -p "$validation/$clust/spade1_knnED"
  changeParameters 1 "TestuHutsa" "$clust/spade1_knnED/evalTrack_hclust-SPADE-knnED.txt" "$clust/spade1_knnED"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A052MainClassHclustSpadeKnnED $parameters

  clust="pam_"$dm
  mkdir -p "$validation/$clust/spade1_knnED"
  changeParameters 1 "TestuHutsa" "$clust/spade1_knnED/evalTrack_pam-SPADE-knnED.txt" "$clust/spade1_knnED"
../jre1.7.0_71/bin/java -Xmx2048m -cp webreco.jar \
  ehupatras.webrecommendation.A053MainClassPamSpadeKnnED $parameters
done > clust_spade_knnED_topic.txt



