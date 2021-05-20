dockers-socket-streaming:
  simulate a log processing cluster in one server.
  simulated cluster consists of one basestation, one Spark master, two Spark workers.
  Spark cluster runs in standalone mode to process log using purely socket streaming.

micro-cluster-lab-master:
  simulate a log processing cluster in one server.
  simulated cluster consists of one basestation, one Spark master, two Spark workers.
  Spark cluster is built upon HDFS with Yarn scheduler.

spark-cluster:
  deploy a log processing cluster in cluster of two servers.
  the read cluster consists of one basestation, one Spark master, two Spark workers.
  Spark cluster is built upon HDFS with Yarn scheduler.
