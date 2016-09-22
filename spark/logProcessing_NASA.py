
# input files
wd = "/home/burdinadar/egitekoak/20160913_spark/ariketa_log_processing/";
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
  # line = 'in24.inetnebr.com - - [01/Aug/1995:00:00:01 -0400] "GET /shuttle/missions/sts-68/news/sts-68-mcc-05.txt HTTP/1.0" 200 1839';
  lineL = line.split(" ");
  if len(lineL)<10:
    return False;
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
  return True;

linesV2 = lines.filter(filterLine);
linesV2 = linesV2.zipWithIndex();
linesV2.count();

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
vectors.count();




###################
### BASIC STATS ###
###################
### What are the statistics for content being returned? Sizes, statuses?

sizes = vectors.map(lambda r: r[8]);
sums = sizes.reduce(lambda a, b: int(a)+int(b));
count = sizes.count();
avg = sums / count;
# 19092

statuses = vectors.map(lambda r: r[7][0]);
pairs = statuses.map(lambda s: (s, 1));
counts = pairs.reduceByKey(lambda a, b: a+b);
counts.take(10);
# [(u'4', 1), (u'3', 327247), (u'2', 3084559), (u'5', 65)]

dataDF = sqlContext.createDataFrame(vectors, ['reqID', 'remotehost', 'rfc931', 'authuser', 'method', 'url', 'http', 'status', 'bytes', 'timestamp', 'tz']);
dataDF.printSchema();

sizesStat = dataDF.describe("bytes");
# [Row(summary=u'count', bytes=u'3411872'), Row(summary=u'mean', bytes=u'19092.70457244586'), Row(summary=u'stddev', bytes=u'73028.70935887514'), Row(summary=u'min', bytes=u'0'), Row(summary=u'max', bytes=u'6823936')]

statuses = dataDF.select("status");
pairs = statuses.map(lambda s: (s, 1));
counts = pairs.reduceByKey(lambda a, b: a+b);
counts.take(10);
# [(Row(status=u'304'), 266764), (Row(status=u'500'), 65), (Row(status=u'404'), 1), (Row(status=u'200'), 3084559), (Row(status=u'302'), 60483)]

### What are the types of return codes?

statuses = vectors.map(lambda r: r[7]);
pairs = statuses.map(lambda s: (s, 1));
counts = pairs.reduceByKey(lambda a, b: a+b);
counts.take(50);
# [(u'200', 3084559), (u'302', 60483), (u'500', 65), (u'304', 266764), (u'404', 1)]

### How many 404 (page not found) errors are there?

error404 = vectors.filter(lambda r: r[7]=="404");
error404.count();
# 1

##################
### TIME STATS ###
##################
# How many unique hosts per day?
dataDF = sqlContext.createDataFrame(vectors, ['reqID', 'remotehost', 'rfc931', 'authuser', 'method', 'url', 'http', 'status', 'bytes', 'timestamp', 'tz']);

def identifyDay(row):
  reqID = row.reqID;
  dt = row.timestamp;
  year = '{0:04d}'.format(dt.year)
  month = '{0:02d}'.format(dt.month)
  day = '{0:02d}'.format(dt.day)
  #hour = '{0:02d}'.format(dt.hour)
  #minute = '{0:02d}'.format(dt.minute)
  #second = '{0:02d}'.format(dt.second)
  dayID = year + month + day;
  return (reqID, dayID);

dayIDs = dataDF.map(identifyDay)
dayIDsDF = sqlContext.createDataFrame(dayIDs, ['reqID', 'dayID']);
dataDF = dataDF.join(dayIDsDF, "reqID");

  # number of requests each day
daysGB = dataDF.groupBy(dataDF.dayID)
daysGB.agg({"reqID": "count"}).collect()
#[Row(dayID=u'19950801', count(1)=33471), Row(dayID=u'19950803', count(1)=40773), Row(dayID=u'19950804', count(1)=58805), Row(dayID=u'19950805', count(1)=31450), Row(dayID=u'19950806', count(1)=31858), Row(dayID=u'19950807', count(1)=56418), Row(dayID=u'19950808', count(1)=59232), Row(dayID=u'19950809', count(1)=59833), Row(dayID=u'19950810', count(1)=60412), Row(dayID=u'19950811', count(1)=60589), Row(dayID=u'19950812', count(1)=37628), Row(dayID=u'19950813', count(1)=36109), Row(dayID=u'19950814', count(1)=59205), Row(dayID=u'19950815', count(1)=58135), Row(dayID=u'19950816', count(1)=56099), Row(dayID=u'19950817', count(1)=58448), Row(dayID=u'19950818', count(1)=55570), Row(dayID=u'19950819', count(1)=31745), Row(dayID=u'19950701', count(1)=63855), Row(dayID=u'19950702', count(1)=59424), Row(dayID=u'19950703', count(1)=88379), Row(dayID=u'19950704', count(1)=69547), Row(dayID=u'19950705', count(1)=93241), Row(dayID=u'19950706', count(1)=99422), Row(dayID=u'19950707', count(1)=85810), Row(dayID=u'19950708', count(1)=38205), Row(dayID=u'19950709', count(1)=34577), Row(dayID=u'19950820', count(1)=32491), Row(dayID=u'19950821', count(1)=54805), Row(dayID=u'19950822', count(1)=57043), Row(dayID=u'19950823', count(1)=57339), Row(dayID=u'19950824', count(1)=51698), Row(dayID=u'19950825', count(1)=56537), Row(dayID=u'19950826', count(1)=31086), Row(dayID=u'19950827', count(1)=32196), Row(dayID=u'19950828', count(1)=54620), Row(dayID=u'19950710', count(1)=71614), Row(dayID=u'19950829', count(1)=67037), Row(dayID=u'19950711', count(1)=79213), Row(dayID=u'19950712', count(1)=90902), Row(dayID=u'19950713', count(1)=132409), Row(dayID=u'19950714', count(1)=82728), Row(dayID=u'19950715', count(1)=44861), Row(dayID=u'19950716', count(1)=47187), Row(dayID=u'19950717', count(1)=73850), Row(dayID=u'19950718', count(1)=63147), Row(dayID=u'19950719', count(1)=71577), Row(dayID=u'19950830', count(1)=79175), Row(dayID=u'19950831', count(1)=88738), Row(dayID=u'19950720', count(1)=65609), Row(dayID=u'19950721', count(1)=63781), Row(dayID=u'19950722', count(1)=34804), Row(dayID=u'19950723', count(1)=38666), Row(dayID=u'19950724', count(1)=63238), Row(dayID=u'19950725', count(1)=61523), Row(dayID=u'19950726', count(1)=57979), Row(dayID=u'19950727', count(1)=60929), Row(dayID=u'19950728', count(1)=26850)]

  # number of request for each IP and each day
daysIpGB = dataDF.groupBy(dataDF.dayID, dataDF.remotehost).count()
daysIpGB.take(10)
#[Row(dayID=u'19950801', remotehost=u'telly.tc.cornell.edu', count=10), Row(dayID=u'19950804', remotehost=u'ix-min1-05.ix.netcom.com', count=11), Row(dayID=u'19950804', remotehost=u'asky.picky.or.jp', count=41), Row(dayID=u'19950807', remotehost=u'mcwy04.med.nyu.edu', count=9), Row(dayID=u'19950807', remotehost=u'hpsystem1.informatik.tu-muenchen.de', count=37), Row(dayID=u'19950809', remotehost=u'192.206.65.144', count=14), Row(dayID=u'19950810', remotehost=u'n1132667.ksc.nasa.gov', count=60), Row(dayID=u'19950811', remotehost=u'193.130.159.249', count=16), Row(dayID=u'19950811', remotehost=u'199.99.242.15', count=103), Row(dayID=u'19950814', remotehost=u'ws_chris.niia.net', count=9)]

  # IPs with one request in each day: unique request
daysCountDF = daysIpDF.map(lambda row: (row.dayID, row.count==1))
daysUniquesDF = daysCountDF.filter(lambda row: row[1]==True)
# []





























####################################################################


dataIdDF = dataDF.select(identifyDayUDF(dataDF.reqID, dataDF.timestamp).alias("reqID-dayID"))



dataDF.map(identifyDay);


kk.take(10)

dataDF.zipWithIndex();
dataDF.printSchema();

dataDF.select((identifyDay(dataDF.timestamp)).alias(dayID));
kk.take(10)

kk = dataDF.map(lambda row: str(row.timestamp.year) + str(row.timestamp.month))
kk.take(10)




kk = dataDF.select("timestamp").map(lambda a: a.timestamp.year)

kk = dataDF.map(lambda a: a.timestamp.year)

df.groupBy('name').


# How many requests per day?
# On average, how many requests per host?
# How many 404 errors per day?






counts.saveAsTextFile(wd + "counts.txt")


















data = ["a", "b", "a", "c"]
data2 = sc.parallelize(data)
dataP = data2.map(lambda s: (s, 1))
dataC = dataP.reduceByKey(lambda a, b: a+b)
dataC.saveAsSequenceFile(wd + "kk")


# create dataframe
dataDF = sqlContext.createDataFrame(vectors, ['remotehost', 'rfc931', 'authuser', 'method', 'url', 'http', 'status', 'bytes', 'timestamp', 'tz']);


statusDF = dataDF.select("status");

statusDF2 = statusDF.flatMap(lambda st: st.status[0]).take(10);


kk = pyspark.sql.Row(status=u'200')



kk = dataDF.groupBy(dataDF.status);

dataDF.filter(dataDF.status.isnumeric())

select("status")
dataDF.filter(dataDF.status.isnumeric()).take(10)

select("status").take(3) map(lambda row: row.status.isnumeric()).take(10)

dataDF.describe("status");

dataDF.select("bytes").take(10)



linesA = lines.map(lambda l: l.split());


# create a Row
  





