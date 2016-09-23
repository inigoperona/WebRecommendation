

wd = "/home/burdinadar/workspace_ehupatras/WebRecommendation/20160922_spark_NASA/"

# load the saved dataframe
dataDF = sqlContext.read.parquet(wd + "nasa.parquet")

# assign an URL identifier to each URL
urlsSeq = dataDF.select("url").distinct().rdd.map(lambda row: row.url)
urlsSeq = urlsSeq.zipWithIndex()
urlsDF = spark.createDataFrame(urlsSeq, ['url', 'urlID'])
dataDF = dataDF.join(urlsDF, "url")

# assign an IP identifier to each IP
ipsSeq = dataDF.select("remotehost").distinct().rdd.map(lambda row: row.remotehost)
ipsSeq = ipsSeq.zipWithIndex()
ipsDF = spark.createDataFrame(ipsSeq, ['remotehost', 'ipID'])
dataDF = dataDF.join(ipsDF, "remotehost")

# order the dataframe
#dataDF2 = dataDF.rdd.sortByKey(True, "reqID")

# create sequences
sesDF = dataDF.rdd.map(lambda row: ((row.ipID, row.logF, row.sesID), [(row.reqID, row.reqID2, row.tsSec)]))

def createSessions(vecA, vecB):
  



sesDF.reduceByKey()




