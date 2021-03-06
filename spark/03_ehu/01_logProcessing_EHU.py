# ./bin/spark-submit $HOME/workspace_ehupatras/WebRecommendation/spark/03_ehu/01_logProcessing_EHU.py
# ./bin/spark-submit --master spark://master:7077 $HOME/spark/SPARK_SCRIPTS/01_logProcessing_NASA.py
# ./bin/spark-submit --num-executors 1 --executor-cores 1 --executor-memory 1g $HOME/workspace_ehupatras/WebRecommendation/spark/03_ehu/01_logProcessing_EHU.py

wd = "EMAITZAK/"

# create SparkContext
from pyspark import SparkContext
sc = SparkContext(appName="logProcessingEHU")

# input files
logFiles = "hdfs://u108019.ehu.es:9000/ehu/access_log.anon.*.txt.gz"
#logFiles = "hdfs://u108019.ehu.es:9000/ehu/access_log.anon.20160326.log.txt.gz"
#logFiles = "hdfs://u108019.ehu.es:9000/ehu_proba/proba100000.txt.gz"
#logFiles = "file:/home/burdinadar/workspace_ehupatras/WebRecommendation/20161005_ehu/proba/proba1000000.txt.gz"
#logFiles = "hdfs://u108019.ehu.es:9000/ehu_proba/proba1000000.txt.gz"

##########################
### read the log files ###
##########################
lines = sc.textFile(logFiles)

##############
# PREPROCESS #
##############


# FILTERING

# cat access_log_*.txt | awk '{l=split($0,a,""); for(i=1;i<=l;i++){maiz[a[i]]++}}END{for(i in maiz){print maiz[i]" "i}}' | sort -n > chars.txt

# check if the line is well formed
import re
def filterLine(line):
  # has the line got enogh fields?
  lineL = line.split(" ");
  if len(lineL)<12:
    return False;
  # is the line well-formed?
  remotehost = lineL[0];
  rfc931 = lineL[1];
  authuser = lineL[2];
  date = lineL[3];
  bol = re.match('^\[', date);
  if not bol:
    return False;
  tz = lineL[4];
  bol = re.match('^.+]$', tz);
  if not bol:
    return False;
  method = lineL[5];
  bol = re.match('^"(GET|POST)$', method);
  if not bol:
    return False;
  url = lineL[6];
  #bol = re.match('^\/', url);
  #if not bol:
  #  return False;
  http = lineL[7];
  bol = re.match('^HTTP', http);
  if not bol:
    return False;
  status = lineL[8];
  bol = status.isdigit();
  if not bol:
    return False;
  bytes = lineL[9];
  bol = bytes.isdigit();
  if not bol:
    return False;
  # reference
  reference = lineL[10];
  # user agent
  useragentL = lineL[11:];
  useragent = ' '.join(useragentL)
  useragent = useragent.lower()
  # bot detection
  if "bot" in useragent:
    return False
  if "spider" in useragent:
    return False
  if "crawler" in useragent:
    return False
  # URL TREATMENT
  # not interesting activity
  urll = url.lower()
  if "admin" in urll:
    return False
  if "error" in urll:
    return False
  if "rss" in urll:
    return False
  # remove the parameters
  url2 = url
  if "?" in url:
    iExclam = url.index('?')
    url2 = url[:(iExclam+1)].lower()
  # URL extensions
  bol = url2[-5:] == ".aspx"
  if not bol:
    return True;
  bol = url2[-4:] == ".pdf"
  if not bol:
    return True;
  bol = url2[-4:] == ".htm"
  if not bol:
    return True;
  bol = url2[-5:] == ".html"
  if not bol:
    return True;
  bol = url2[-4:] == ".doc"
  if not bol:
    return True;
  bol = url2[-4:] == ".xml"
  if not bol:
    return True;
  bol = url2[-3:] == ".do"
  if not bol:
    return True;
  # it does not have any extension
  bol = "." not in url2[-5:]
  if bol:
    return True;
  else:
    return False;


# MAP: extract fields

# function that reads a line and converts to vector
import time;
import datetime;
def interpretLine(line):
  #line = lineAndID[0];
  #reqID = lineAndID[1];
  # line = 'in24.inetnebr.com - - [01/Aug/1995:00:00:01 -0400] "GET /shuttle/missions/sts-68/news/sts-68-mcc-05.txt HTTP/1.0" 200 1839';
  lineL = line.split(" ");
  remotehost = lineL[0];
  rfc931 = lineL[1];
  authuser = lineL[2];
  method = lineL[5];
  method2 = method[1:];
  url = lineL[6];
  http = lineL[7];
  http2 = http[:len(http)-1];
  status = lineL[8];
  bytes = int(lineL[9]);
  # reference
  reference = lineL[10];
  reference2 = reference[1:len(reference)-1]
  # user agent
  useragentL = lineL[11:];
  useragent = '_'.join(useragentL)
  useragent2 = useragent[1:len(useragent)-2]
  # datetime
  date = lineL[3];
  date2 = date[1:];
  tz = lineL[4];
  tz2 = tz[:len(tz)-1];
  timestamp = time.strptime(date2, "%d/%b/%Y:%H:%M:%S");
  #timestamp2 = time.mktime(timestamp);
  timestamp2 = datetime.datetime(timestamp.tm_year, timestamp.tm_mon, timestamp.tm_mday, timestamp.tm_hour, timestamp.tm_min, timestamp.tm_sec);
  # print
  #print "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s" \
  #% (remotehost, rfc931, authuser, \
  #   timestamp, tz, method2, url, http2, \
  #   status, bytes)
  # create a processed line
  vector = (remotehost, rfc931, authuser, method2, url, http2, status, bytes, \
            timestamp2, tz2, \
            reference2, useragent2);
  return vector;


# EXEC
vectors = lines.filter(filterLine) \
               .map(interpretLine) \
               .zipWithUniqueId() \
               .map(lambda (line, reqID): (reqID,) + line)

# MAP: identifying day

def identifyDay(row):
  reqID = row[0]
  dt = row[9]
  #dtMod = dt + timedelta(hours=3)
  year = '{0:04d}'.format(dt.year)
  month = '{0:02d}'.format(dt.month)
  day = '{0:02d}'.format(dt.day)
  #hour = '{0:02d}'.format(dt.hour)
  #minute = '{0:02d}'.format(dt.minute)
  #second = '{0:02d}'.format(dt.second)
  dayID = year + month + day;
  return row + (dayID,)

vectors = vectors.map(identifyDay)


# MAP: timestamp in seconds

def timeToSeconds(row):
  reqID = row[0];
  dt = row[9];
  ts = time.mktime(dt.timetuple())
  return row + (ts,)

vectors = vectors.map(timeToSeconds)
# vectors.saveAsTextFile(wd + "ehu_vectors.txt")

vectors.saveAsTextFile(wd + "ehu00_filtering2.txt",
          compressionCodecClass="org.apache.hadoop.io.compress.GzipCodec")

##################
### SESSIONING ###
##################

# MAIN VECTOR
# key: (remotehost)
# vec: 0:reqID, 1:remotehost, 2:rfc931, 3:authuser, 4:method, 5:url, 6:http, 7:status, 8:bytes, 9:timestamp, 10:tz, 11:reference, 12:useragent, 13:dayID, 14:tsSec
vectors2 = vectors.map(lambda vec: ((vec[13], vec[1]), vec))
print("partitionBy: " + str(vectors2.count()))

# PARTITIONING
partSize = 50000
nElems = vectors2.count()
nParts = nElems / partSize
vectors3 = vectors2.partitionBy(nParts)
print("partitionBy: " + str(vectors3.count()))
# vectors3.getNumPartitions()
# vectors3.saveAsTextFile(wd + "ehu_vectors3.txt")

# SESIONNING
def mySort(iterator):
  ema = []
  for vec1 in iterator:
    ts1 = vec1[1]
    n = len(ema)
    i = n - 1
    while i >= 0:
      vec0 = ema[i]
      ts0 = vec0[1]
      if ts1 >= ts0:
        ema.insert(i+1,vec1)
        break
      i = i - 1
    if i == -1:
      ema.insert(0,vec1)
  return ema

def computeElapsedTime2(iterator):
  ema = []
  sesID = -1
  tupOld = (-1, -1, -1)
  for vec in iterator: 
    ip = vec[0][0]
    ts = vec[0][1]
    reqID = vec[0][2]
    tupCurrent = (ip, ts, reqID)
    ipOld = tupOld[0]
    if ip != ipOld:
      if ipOld != -1:
        ema = ema + [(tupOld[2], (tupOld[0], -1, iSes, iReq, sesTime))]
      tupOld = tupCurrent
      i = 0
      iSes = 0
      iReq = 0
      sesTime = 0
    else:
      timeOld = tupOld[1]
      timeCurrent = tupCurrent[1]
      diffTime = timeCurrent - timeOld
      diffTime = int(diffTime)
      if diffTime<=600:
        ema = ema + [(tupOld[2], (tupOld[0], diffTime, iSes, iReq, sesTime))]
        iReq = iReq + 1
        sesTime = sesTime + diffTime
      else:
        ema = ema + [(tupOld[2], (tupOld[0], -1, iSes, iReq, 0))]
        iSes = iSes+1
        iReq = 0
        sesTime = 0
      tupOld = tupCurrent
    i = i+1
  ema = ema + [(tupCurrent[2], (tupCurrent[0], -1, iSes, iReq, sesTime))]
  return ema

# vector: ((remotehost, timestamp, reqID), 0)
kk = vectors3.mapPartitions(mySort)

ses 



map(lambda v: (v[1][1], v[1][14], v[1][0])) \
              .sortByKey() \
              .mapPartitions(computeElapsedTime2)
print("Start saveAsTextFile: ehu_ses2.txt")
ses.saveAsTextFile(wd + "ehu_ses2.txt",
     compressionCodecClass="org.apache.hadoop.io.compress.GzipCodec")
# ses: (reqID, (remotehost, diffTime, ises, ireq, sesTime))


# join
print("Start join")
vector4 = vectors3.map(lambda (a, b): (b[0], b)) \
                  .join(ses)
print("Start saveAsTextFile: ehu_join2.txt")
vector4.saveAsTextFile(wd + "ehu_join2.txt",
          compressionCodecClass="org.apache.hadoop.io.compress.GzipCodec")
# vector4.getNumPartitions()
print("Start coalesce")
#vector5 = vector4.coalesce(nParts) \
vector5 = vector4.map(lambda (a,b): b[0] + b[1][1:])
# vector5.getNumPartitions()
print("Start saveAsTextFile: ehu_data2.txt")
vector5.saveAsTextFile(wd + "ehu_data2.txt",
          compressionCodecClass="org.apache.hadoop.io.compress.GzipCodec")
#vector5.saveAsPickleFile(wd + "ehu_data.p")
#vector5.map(lambda v: (v[0], v[:8]+v[10:])) \
#       .saveAsSequenceFile(wd + "ehu_data2.s")
#vector5.map(lambda v: (v[0], v[:8]+v[10:])) \
#       .saveAsHadoopFile(wd + "ehu_data.h",
#         "org.apache.hadoop.mapred.TextOutputFormat",
#         compressionCodecClass="org.apache.hadoop.io.compress.GzipCodec")


# vec: 0:reqID, 1:remotehost, 2:rfc931, 3:authuser, 
#      4:method, 5:url, 6:http, 7:status, 8:bytes, 
#      9:timestamp, 10:tz, 11:reference, 12:useragent, 13:dayID, 14:tsSec, 
#      15:elapTime, 16:idSes2, 17:idReq2, 18:sesTime

