#!/bin/bash

pre="00_preprocess"
dbs="01_databases"
val="02_validation"

mkdir -p $pre
mkdir -p $dbs
mkdir -p $val

# MODEL GENERATION & EVALUATION: PAM + SPADE
dm="DM_ED"
clust="/pam_DM_ED"
mkdir -p $val$clust"/spade"
java -Xmx4G -cp webreco.jar ehupatras.webrecommendation.A054MainClassPAMSpadecv2 \
  $pre "/T1T2_SARRERA-ACCESO_SEKUENTZIAK_sessionID_urlID_berriak_jarraian_OK_2.txt" "5" ".javaData" \
  "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" "/empty.txt" \
  $dbs "/$dm" \
  $val $clust $clust"/spade" "/pam_spade_eval.txt" \
  "-" 1 > 06_spade.txt

