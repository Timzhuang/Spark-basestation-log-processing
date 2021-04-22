import timeit
from decoder_v2 import mi_enb_decoder
#from mobile_insight_enb.mobile_insight.monitor.decoder import mi_enb_decoder
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext

def process_packet(line):
    packet = mi_enb_decoder(line)
    type_id = packet.get_type_id()
    if (type_id is not None):
        data = packet.get_content()
        if (data is not None):
            return {"time": timeit.default_timer(), "type_id": type_id, "data": data}
    return {}


# cluster-spark-basic.py
from pyspark import SparkConf
from pyspark import SparkContext

conf = SparkConf()
conf.setMaster('spark://172.18.0.22:7077')
conf.setAppName('basestation-analyze')
sc = SparkContext(conf=conf)
ssc = StreamingContext(sc, 10)

packets = ssc.textFileStream("./data/")
msgs  = packets.map(lambda packet: process_packet(packet)).filter(lambda msg: msg != {})

count = msgs.count()
count.pprint()

#msgs.pprint()
#msgs.saveAsTextFiles("./output/out");

ssc.start()
ssc.awaitTermination()
