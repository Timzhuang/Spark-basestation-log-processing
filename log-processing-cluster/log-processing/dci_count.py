from pyspark import SparkContext
from pyspark.streaming import StreamingContext
import binascii

sc = SparkContext("spark://vagrant.vm:7077", "NetworkWordCount")
sc.setLogLevel("ERROR")
ssc = StreamingContext(sc, 1)
ssc.checkpoint("checkpoint")
initialStateRDD = sc.parallelize([])

stream_data = ssc.textFileStream("file:////home/vagrant/219/data")

def decode_line(line):
    try:
        l = self.packet.split(" ")
        if l[1] == "DCI":
            return l[2]
        else:
            return None
    except:
        return None

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