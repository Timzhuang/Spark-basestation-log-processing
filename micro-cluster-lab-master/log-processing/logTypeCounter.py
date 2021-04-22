import timeit
from decoder_v2 import mi_enb_decoder
#from mobile_insight_enb.mobile_insight.monitor.decoder import mi_enb_decoder
from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext

def decode_packet(line):
    packet = mi_enb_decoder(line)
    type_name = packet.get_type_id()
    return type_name if type_name is not None else ""


conf = SparkConf()
conf.setMaster('spark://172.18.0.22:7077')
conf.setAppName('basestation-analyze')
sc = SparkContext(conf=conf)
ssc = StreamingContext(sc, 10)

packets = ssc.textFileStream("/mnt/nfs_clientshare/data")
count  = packets.map(lambda packet: (decode_packet(packet), 1)).filter(lambda x: x[0] != "").reduceByKey(lambda x,y: x+y)

count.pprint()

#msgs.pprint()
#msgs.saveAsTextFiles("./output/out");

ssc.start()
ssc.awaitTermination()
