

wd = "/home/burdinadar/workspace_ehupatras/WebRecommendation/20160922_spark_NASA/";

# load the saved dataframe
dataDF = sqlContext.read.parquet(wd + "nasa.parquet")

# assign an URL identifier to each URL
urlsSeq = dataDF.select("url").distinct().map(lambda row: row.url)
urlsSeq = urlsSeq.zipWithIndex()
urlsDF = sqlContext.createDataFrame(urlsSeq, ['url', 'urlID'])
dataDF = dataDF.join(urlsDF, "url")

# assign an IP identifier to each IP
ipsSeq = dataDF.select("remotehost").distinct().map(lambda row: row.remotehost)
ipsSeq = ipsSeq.zipWithIndex()
ipsDF = sqlContext.createDataFrame(ipsSeq, ['remotehost', 'ipID'])
dataDF = dataDF.join(ipsDF, "remotehost")

# create sequences
ipDF = dataDF.map(lambda row: (row.ipID, [(row.reqID, row.sesID, reqID2, tsSec)]))


