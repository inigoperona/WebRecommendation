#!/bin/bash

# head -n+100000 BidasoaTurismo_LOGs_from_Jan9_toNov19.log > log_bt.txt

pre="00_preprocess"
dbs="01_databases"
val="02_validation"

mkdir -p $pre
mkdir -p $dbs
mkdir -p $val

# PREPROCESS
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar angelu.webrecommendation.A000MainClassPreprocess \
  $pre "/BidasoaTurismo_LOGs_from_Jan9_toNov19.log" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  "xxx" "xxx" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 00_preprocessing.txt

jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar angelu.webrecommendation.A001MainClassCreateDatabase \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "xxx" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 01_createDB.txt

# DISTANCE MATRIX
dm="DM_ED"
mkdir -p $dbs/$dm
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar angelu.webrecommendation.A012MainClassDistanceMatrixED \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrixED.txt

dm="DM_LG"
mkdir -p $dbs/$dm
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A011MainClassDistanceMatrixLGInverse \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrixLG.txt

# DATABASE SPLITS FOR EVALUATION
dm="DM_ED"
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar angelu.webrecommendation.A021MainClassCrossValidation \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val "xxx" "xxx" "xxx" \
  "-" 1 > 03_crossvalidation.txt

# MODEL GENERATION & EVALUATION: Markov Chain
dm="DM_ED"
mc="/MC_DM_ED"
mkdir -p $val$mc
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A030MainClassMarkovChaincv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $mc "xxx" "/eval.txt" \
  "-" 1 > 04_mc.txt

# MODEL GENERATION & EVALUATION: Global Suffix Tree
dm="DM_ED"
gst="/GST_DM_ED"
mkdir -p $val$gst
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1Suffixcv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $gst "xxx" "/eval.txt" \
  "-" 1 > 04_gst.txt

# MODEL GENERATION & EVALUATION: PAM + SPADE + knnED
dm="DM_ED"
clust="/pam_DM_ED"
mkdir -p $val$clust
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar angelu.webrecommendation.A041MainClassPAMcv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust "xxx" "xxx" \
  "-" 1 > 05_pam.txt

mkdir -p $val$clust"/spade1"
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A053MainClassPamSpadeKnnEDcv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust $clust"/spade1" "/pam_spade1_knnED_eval.txt" \
  "-" 1 > 06_evaluationPAMknnED.txt

# MODEL GENERATION & EVALUATION: hclust + MAS.Wseq + ST
dm="DM_LG"
clust="/hclust_DM_LG"
mkdir -p $val$clust
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A040MainClassHclustcv
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust "xxx" "xxx" \
  "-" 1 > 05_hclust.txt

mkdir -p $val$clust"/msa"
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A0500MainClassHclustMsaStcv
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust $clust"/msa" "/hclust_msa_wseq_st_eval.txt" \
  "-" 1 > 06_evaluationHclustMSAWseqST.txt

# MODEL GENERATION & EVALUATION: PAM/hclust + SPADE + ST
dm="DM_ED"
clust="/pam_DM_ED"
mkdir -p $val$clust"/spade"
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A054MainClassPAMSpadeStcv
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust $clust"/spade" "/pam_spade_st_eval.txt" \
  "-" 1 > 07_evaluationPAMSpadeST.txt

dm="DM_LG"
clust="/hclust_DM_LG"
mkdir -p $val$clust"/spade"
jre/jre1.8.0_91/bin/java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A054MainClassHclustSpadeStcv
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust $clust"/spade" "/hclust_spade_st_eval.txt" \
  "-" 1 > 07_evaluationHclustSpadeST.txt

