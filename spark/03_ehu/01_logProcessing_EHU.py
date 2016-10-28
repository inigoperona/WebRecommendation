# ./bin/spark-submit $HOME/workspace_ehupatras/WebRecommendation/spark/03_ehu/01_logProcessing_EHU.py
# ./bin/spark-submit --master spark://master:7077 $HOME/spark/SPARK_SCRIPTS/01_logProcessing_NASA.py

wd = "../EMAITZAK/"

# create SparkContext
from pyspark import SparkContext
sc = SparkContext(appName="logProcessingEHU")

# input files
logFiles = "hdfs://u108019.ehu.es:9000/ehu/access_log.anon.*.txt.gz"
#logFiles = "hdfs://u108019.ehu.es:9000/ehu/access_log.anon.20160326.log.txt.gz"
#logFiles = "hdfs://u108019.ehu.es:9000/ehu_proba/proba100000.txt.gz"
#logFiles = "file:/home/burdinadar/workspace_ehupatras/WebRecommendation/20161005_ehu/proba/proba100000.txt.gz"
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



##################
### SESSIONING ###
##################

# MAIN VECTOR
# key: (remotehost)
# vec: 0:reqID, 1:remotehost, 2:rfc931, 3:authuser, 4:method, 5:url, 6:http, 7:status, 8:bytes, 9:timestamp, 10:tz, 11:reference, 12:useragent, 13:dayID, 14:tsSec
vectors2 = vectors.map(lambda vec: ((vec[1]), vec))

# PARTITIONING
partSize = 10000
nElems = vectors2.count()
nParts = nElems / partSize
vectors3 = vectors2.partitionBy(nParts).persist()

# SESIONNING
def orderRequests(iterator):
  hiz = {}
  for elemCurrent in iterator:
    keyCurrent = elemCurrent[0]
    tupCurrent = elemCurrent[1]
    vecNew = []
    if keyCurrent in hiz:
      timeCurrent = tupCurrent[1]
      vecOld = hiz[keyCurrent]
      lenVecOld = len(vecOld)
      i = 0
      while i<lenVecOld:
        tupOldi = vecOld[i]
        timeOldi = tupOldi[1]
        if timeCurrent < timeOldi:
          vecNew = vecNew + [tupCurrent] + vecOld[i:]
          break
        else:
          vecNew = vecNew + [tupOldi]
        i = i + 1
      if i==lenVecOld:
        vecNew = vecNew + [tupCurrent]
    else:
      vecNew = [tupCurrent]
    hiz[keyCurrent] = vecNew
  # convert dictionary to vector
  ema = []
  for k in hiz.keys():
    ema = ema + [(k, hiz[k])]
  return ema


def computeElapsedTime(row):
  vec = row[1]
  isFirstReq = True
  i = 0
  iSes = 0
  iReq = 0
  sesTime = 0
  ema = []
  while i<len(vec):
    tupCurrent = vec[i]
    if isFirstReq:  
      tupOld = tupCurrent
      isFirstReq = False
    else:
      timeOld = tupOld[1]
      timeCurrent = tupCurrent[1]
      diffTime = timeCurrent - timeOld
      diffTime = int(diffTime)
      if diffTime<=600:
        ema = ema + [(tupOld[0], diffTime, iSes, iReq, sesTime)]
        iReq = iReq + 1
        sesTime = sesTime + diffTime
      else:
        ema = ema + [(tupOld[0], -1, iSes, iReq, 0)]
        iSes = iSes+1
        iReq = 0
        sesTime = 0
      tupOld = tupCurrent
    i = i+1
  ema = ema + [(tupCurrent[0], -1, iSes, iReq, sesTime)]
  return ema

ses = vectors3.map(lambda v: ((v[1][1]), (v[1][0], v[1][14]))) \
               .mapPartitions(orderRequests) \
               .flatMap(computeElapsedTime) \
               .map(lambda (a,b,c,d,e): [a, (b,c,d,e)])
#ses.saveAsTextFile(wd + "ehu_ses.txt")
ses.saveAsPickleFile(wd + "ehu_ses.pickle")

# join
vector4 = vectors3.map(lambda (a, b): (b[0], b)) \
                  .join(ses) \
                  .map(lambda (a,b): b[0] + b[1])
# vec: 0:reqID, 1:remotehost, 2:rfc931, 3:authuser, 
#      4:method, 5:url, 6:http, 7:status, 8:bytes, 
#      9:timestamp, 10:tz, 11:reference, 12:useragent, 13:dayID, 14:tsSec, 
#      15:elapTime, 16:idSes2, 17:idReq2, 18:sesTime

# WRITE the database
vector4.saveAsPickleFile(wd + "ehu_data.pickle")
#vector4.saveAsTextFile(wd + "ehu_data.txt")

