#!/bin/bash

file_no=0

rm -r /root/data
mkdir /root/data
hdfs dfs -rm -r /data
hdfs dfs -mkdir /data

while :
do
    timeout 10s nc basestation 12345 > /root/data/out$file_no
    hdfs dfs -put /root/data/out$file_no /data/$file_no
    ((file_no++))
    sleep 1s
done
