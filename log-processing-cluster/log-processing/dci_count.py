from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
import binascii

conf = SparkConf()
conf.setAppName('basestation-analyze')
sc = SparkContext(conf=conf)
sc.setLogLevel("ERROR")
ssc = StreamingContext(sc, 10)
ssc.checkpoint("checkpoint")

initialStateRDD = sc.parallelize([])

stream_data = ssc.textFileStream("hdfs:///data/")

def decode_line(line):
    try:
        l = line.split(" ")
        if l[1] == "DCI":
            return [l[2]]
        else:
            return [""]
    except:
        return [""]

def message_count(m):
    return m, 1

def update_count(new_values, last_sum):
    return sum(new_values) + (last_sum or 0)

stream_data = stream_data.flatMap(decode_line)
stream_data = stream_data.map(message_count)
stream_data = stream_data.updateStateByKey(
    update_count, initialRDD=initialStateRDD)

stream_data.pprint()

ssc.start()
ssc.awaitTermination()
