#!/bin/bash

# head -n+100000 BidasoaTurismo_LOGs_from_Jan9_toNov19.log > log_bt.txt

pre="00_preprocess"
dbs="01_databases"
val="02_validation"

mkdir -p $pre
mkdir -p $dbs
mkdir -p $val

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

dm="DM_ED"
mkdir -p $dbs/$dm
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A012MainClassDistanceMatrixED \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrix.txt

java -Xmx4G -cp webreco.jar angelu.webrecommendation.A021MainClassCrossValidation \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val "xxx" "xxx" "xxx" \
  "-" 1 > 03_crossvalidation.txt

pam="/pam_DM_ED"
mkdir -p $val$pam
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A041MainClassPAMholdout \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $pam "xxx" "xxx" \
  "-" 1 > 04_pam.txt

mkdir -p $val$pam"/spade1"
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop1 \
  $pre "log_bt.txt" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $pam $pam"/spade1" "evaluation.txt" \
  "-" 1 > 05_evaluation.txt


