## build docker images
docker build -f Dockerfile_base -t base .
docker build -f Dockerfile_master -t spark_master .
docker build -f Dockerfile_worker -t spark_worker .
docker build -f Dockerfile_client -t spark_submit .
docker build -f Dockerfile_basestation -t basestation .

## set up the cluster

## Create user defined bridge network inside of Docker using
docker network create -d bridge spark-net 

## Create a spark-master container inside of the bridge network
docker run -dit --name spark-master --network spark-net -p 8080:8080 spark_master bash

## Run a worker container
docker run -dit --name spark-worker1 --network spark-net -p 8081:8081 -e MEMORY=2G -e CORES=1 spark_worker bash # run this in multiple terminals with different port mapping and names to run many workers

## run the base station
docker run -dit --name basestation --rm --privileged --cpus="2" --network spark-net basestation # run this in multiple terminals to run many basestations

## Spin-up a spark_submit container
docker run -it --name spark-submit --network spark-net -p 4040:4040 spark_submit bash

## run codes in the spark_submit container which is the workplace



