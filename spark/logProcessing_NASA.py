
# input files
wd = "/home/burdinadar/workspace_ehupatras/WebRecommendation/20160922_spark_NASA/";
log1 = wd + "access_log_Aug95.txt";
log2 = wd + "access_log_Jul95.txt";

# read the input files
lines1 = sc.textFile(log1);
lines2 = sc.textFile(log2);
lines = lines1.union(lines2);

#############
# FILTERING #
#############
# cat access_log_*.txt | awk '{l=split($0,a,""); for(i=1;i<=l;i++){maiz[a[i]]++}}END{for(i in maiz){print maiz[i]" "i}}' | sort -n > chars.txt

# check if the line is well formed
import re;
def filterLine(line):
  # has the line got enogh fields?
  lineL = line.split(" ");
  if len(lineL)<10:
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
  bol = re.match('^\/', url);
  if not bol:
    return False;
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
  # URL treatment
  url2 = url
  if "?" in url:
    iExclam = url.index('?')
    url2 = url[:(iExclam+1)]
  bol2 = url2[-5:] == ".html"
  if bol2:
    return True;
  bol2 = "." not in url2[-5:] and url2[:-1] == "/"
  if bol2:
    return True;
  ext = url2[-4:]
  bol2 = ext == ".txt"
  if bol2:
    return True;
  bol2 = ext == ".htm"
  if bol2:
    return True;
  bol2 = ext == ".gov"
  if bol2:
    return True;
  else:
    return False;

linesV2 = lines.filter(filterLine);
linesV2 = linesV2.zipWithIndex();
#linesV2.count();

#######
# MAP #
#######
# function that reads a line and converts to vector
import time;
import datetime;
def interpretLine(lineAndID):
  line = lineAndID[0];
  reqID = lineAndID[1];
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
  vector = (reqID, remotehost, rfc931, authuser, method2, url, http2, status, bytes, timestamp2, tz2);
  return vector;

vectors = linesV2.map(interpretLine);
#vectors.count();



##################
### SESSIONING ###
##################
# create data frame
dataDF = sqlContext.createDataFrame(vectors, ['reqID', 'remotehost', 'rfc931', 'authuser', 'method', 'url', 'http', 'status', 'bytes', 'timestamp', 'tz']);

# add timestamp in seconds
def timeToSeconds(row):
  reqID = row.reqID;
  dt = row.timestamp;
  ts = time.mktime(dt.timetuple())
  return (reqID, ts);

seconds = dataDF.map(timeToSeconds)
secondsDF = sqlContext.createDataFrame(seconds, ['reqID', 'tsSec'])
dataDF = dataDF.join(secondsDF, "reqID")

# order the requests for each IP
ipDF = dataDF.map(lambda row: (row.remotehost, [(row.reqID, row.tsSec)]))

def orderRequests(vecA, vecB):
  lenA = len(vecA)
  lenB = len(vecB)
  iA = 0
  iB = 0
  ema = []
  while iA<lenA or iB<lenB:
    if iA<lenA:
      tupA = vecA[iA]
      timeA = tupA[1]
    else:
      timeA = 9999999999
    if iB<lenB:
      tupB = vecB[iB]
      timeB = tupB[1]
    else:
      timeB = 9999999999
    # compare the rimes
    if timeA<=timeB:
      ema = ema + [tupA]
      iA = iA+1
    else:
      ema = ema + [tupB]
      iB = iB+1
  return ema

ipRB = ipDF.reduceByKey(orderRequests)

# compute the elapsed time between session-consecutive-requests
def computeElapsedTime(row):
  vec = row[1]
  isFirstReq = True
  i = 0
  iSes = 0
  iReq = 0
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
        ema = ema + [(tupOld[0], diffTime, iSes, iReq)]
        iReq = iReq+1
      else:
        ema = ema + [(tupOld[0], -1, iSes, iReq)]
        iSes = iSes+1
        iReq = 0
      tupOld = tupCurrent
    i = i+1
  ema = ema + [(tupCurrent[0], -1, iSes, iReq)]
  return ema

sessions = ipRB.flatMap(computeElapsedTime)

# add session information to the main database
sessionsDF = sqlContext.createDataFrame(sessions, ['reqID', 'elapTime', 'sesID', 'reqID2'])
dataDF = dataDF.join(sessionsDF, "reqID")

# save the dataframe
dataDF.write.save(wd + "nasa.parquet")
dataDF.write.save(wd + "nasa.json", format="json")

