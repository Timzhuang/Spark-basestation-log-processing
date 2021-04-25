# Step1: build images for spark node and basestation
sudo docker build -f Dockerfile_base -t cluster-base .
sudo docker build -f Dockerfile_basestation -t basestation .

# Step 2: start docker container
sudo docker-compose up -d

# Step 3: start the first terminal for the container of node-master (spark master)
sudo docker exec -it node-master bash

#-----------------inside the first container terminal----------------------
# start receiving log data from the basestation
cd /root/
./logReceiver.sh # use control-z, bg 1, kill $! to terminate it
#--------------------------------------------------------------------------


# Step 4: start the second terminal for the container of node-master (spark master)
sudo docker exec -it node-master bash

#-----------------inside the second container terminal----------------------
# start spark to processing logs
cd /root/
spark-submit --master yarn logCounter.py
#--------------------------------------------------------------------------

# Step 5: stop and remove docker containers
sudo docker-compose down
