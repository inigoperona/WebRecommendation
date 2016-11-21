#!/bin/bash

# head -n+100000 BidasoaTurismo_LOGs_from_Jan9_toNov19.log > log_bt.txt

pre="00_preprocess"
dbs="01_databases"
val="02_validation"

mkdir -p $pre
mkdir -p $dbs
mkdir -p $val

# DISTANCE MATRIX
dm="DM_ED"
mkdir -p $dbs/$dm
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A012MainClassDistanceMatrixED2 \
  $pre "/T1T2_SARRERA-ACCESO_SEKUENTZIAK_sessionID_urlID_berriak_jarraian_OK_2.txt" "-1" "xxx" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  "xxx" "xxx" "xxx" "xxx" \
  "-" 1 > 02_distmatrixED.txt

# DATABASE SPLITS FOR EVALUATION
dm="DM_ED"
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A021MainClassCrossValidation2 \
  $pre "/T1T2_SARRERA-ACCESO_SEKUENTZIAK_sessionID_urlID_berriak_jarraian_OK_2.txt" "5" ".javaData" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val "xxx" "xxx" "xxx" \
  "-" 1 > 03_crossvalidation.txt

# MODEL GENERATION & EVALUATION: PAM + SPADE + knnED
dm="DM_ED"
clust="/pam_DM_ED"
mkdir -p $val$clust
java -Xmx4G -cp webreco.jar angelu.webrecommendation.A041MainClassPAMcv2 \
  $pre "/T1T2_SARRERA-ACCESO_SEKUENTZIAK_sessionID_urlID_berriak_jarraian_OK_2.txt" "5" ".javaData" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust "xxx" "xxx" \
  "-" 1 > 05_pam.txt

