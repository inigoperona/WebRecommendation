#!/bin/bash

# head -n+100000 BidasoaTurismo_LOGs_from_Jan9_toNov19.log > log_bt.txt

pre="00_preprocess"
dbs="01_databases"
val="02_validation"

mkdir -p $pre
mkdir -p $dbs
mkdir -p $val

# PREPROCESS
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A000MainClassPreprocess \
  $pre "/log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  "xxx" "xxx" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 00_preprocessing.txt

java -Xmx4G -cp webreco.jar angelu.webrecommendation.A001MainClassCreateDatabase \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "xxx" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 01_createDB.txt

# DISTANCE MATRIX
dm="DM_ED"
mkdir -p $dbs/$dm
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A012MainClassDistanceMatrixED \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrixED.txt

dm="DM_LG"
mkdir -p $dbs/$dm
java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A011MainClassDistanceMatrixLGInverse \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrixLG.txt

# DATABASE SPLITS FOR EVALUATION
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A021MainClassCrossValidation \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val "xxx" "xxx" "xxx" \
  "-" 1 > 03_crossvalidation.txt

# MODEL GENERATION & EVALUATION: Markov Chain
java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A030MainClassMarkovChaincv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $pam "xxx" "xxx" \
  "-" 1 > 04_pam.txt
# kkkkkkkkkkkkkkkkkkkkkkkkkk

# MODEL GENERATION & EVALUATION: Global Suffix Tree
# ehupatras.webrecommendation.
# A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1Suffixcv

# MODEL GENERATION & EVALUATION: PAM + knnED
pam="/pam_DM_ED"
mkdir -p $val$pam
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A041MainClassPAMcv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $pam "xxx" "xxx" \
  "-" 1 > 04_pam.txt

mkdir -p $val$pam"/spade1"
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnEDcv \
  $pre "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $pam $pam"/spade1" "evaluation.txt" \
  "-" 1 > 05_evaluationPAMknnED.txt

# MODEL GENERATION & EVALUATION


