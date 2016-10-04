

wd = "/home/burdinadar/workspace_ehupatras/WebRecommendation/20160922_spark_NASA/"

# load the saved dataframe
dataDF = sqlContext.read.parquet(wd + "nasa.parquet")

# assign an URL identifier to each URL
urlsSeq = dataDF.select("url").distinct().rdd.map(lambda row: row.url)
urlsSeq = urlsSeq.zipWithIndex()
urlsDF = spark.createDataFrame(urlsSeq, ['url', 'urlID'])
urlsDF.write.save(wd + "urlIDs.json", format="json")
dataDF = dataDF.join(urlsDF, "url")

# assign an IP identifier to each IP
ipsSeq = dataDF.select("remotehost").distinct().rdd.map(lambda row: row.remotehost)
ipsSeq = ipsSeq.zipWithIndex()
ipsDF = spark.createDataFrame(ipsSeq, ['remotehost', 'ipID'])
ipsDF.write.save(wd + "ipIDs.json", format="json")
dataDF = dataDF.join(ipsDF, "remotehost")

# create sequences
sesRDD = dataDF.rdd.map(lambda row: ((row.logF, row.ipID, row.sesID, row.reqID2), [(row.reqID, row.urlID)]))
sesRDD = sesRDD.sortByKey(True)
sesRDD.saveAsTextFile(wd + "sessions.txt")

# stat of sequences
sesRDD = dataDF.rdd.map(lambda row: ((row.logF, row.ipID, row.sesID), 1))
sesStat = sesRDD.reduceByKey(lambda a, b: a+b)
sesStat.filter(lambda (sesID, nElem): nElem==1).count() # 139612
sesStat.filter(lambda (sesID, nElem): nElem==2).count() # 55651
sesStat.filter(lambda (sesID, nElem): nElem==3).count() # 30994

