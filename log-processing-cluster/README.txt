**********************************************ditributed test 2**********************************

==========on server 1=====================


------configuration-------------
# start a swarm network in the master machine
docker swarm init

# set a machine to be master
sudo docker node update --label-add role=master ID # we can find the ID of the machine by sudo docker node ls
----------------------

-------testing-----
# Step1: build images for spark node and basestation
sudo docker build -f Dockerfile_base -t cluster-base .
sudo docker build -f Dockerfile_basestation -t basestation .

# step2: create network
sudo docker network create --driver overlay --attachable cluster-network


# Step 3: start docker containers
sudo docker-compose up -d

# step 4: start the first terminal for the container of node-master
sudo docker exec -it node-master bash
%%%%%%%%%%%%%%%%%%%%%%%%inside the first container terminal%%%%%%%%%%%%%%%%%%%%%%%%
# after the ssh server is restarted on node-slave2, we restart yarn
stop-yarn.sh
start-yarn.sh

# start receiving log data from the basestation
cd /root/
./logReceiver.sh # use control-z, bg 1, kill $! to terminate it
%%%%%%%%%%%%%%%%%%%%%%%%


# Step 5: start the second terminal for the container of node-master (spark master)
sudo docker exec -it node-master bash
%%%%%%%%%%%%%%%%%%%%%%%%inside the second container terminal%%%%%%%%%%%%%%%%%%%%%%%%
# start spark to processing logs
cd /root/
spark-submit --master yarn logCounter.py
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


# close up
sudo docker-compose down
sudo docker network rm cluster-network
----------------
=============================


==========on server 2=====================


------configuration-------------
# add a new worker machine to this swarm(cluster network), run the following command in the new worker machine
sudo docker swarm join --token SWMTKN-1-14kpa7cv0t2wwoy8qtvyz3w6hsr5gamamprziheunggybiq4t3-3z7h8enoy01881j6m3sbbybk7 131.179.176.31:2377

# set a machine to be worker
sudo docker node update --label-add role=worker ID
----------------------

-------testing-----


# Step 1: build images for spark node
sudo docker build -f Dockerfile_base -t cluster-base .


# Step 2: start docker containers (after step2 in server1)
sudo docker run -dit --name node-slave2 --hostname node-slave2 --network-alias=[node-slave2] --network cluster-network -p 9043:8042 cluster-base:latest

# step 4: start the first terminal for the container of node-slave2
sudo docker exec -it node-slave2 bash
%%%%%%%%%%%%%%%%%%%%%%%%inside the first container terminal%%%%%%%%%%%%%%%%%%%%%%%%
# copy ssh key and restart ssh server
from ~/.ssh/id_rsa.pub to ~/.ssh/authorized_keys
/etc/init.d/ssh restart

# start datanode
hadoop-daemon.sh start datanode
%%%%%%%%%%%%%%%%%%%%%%%%

# close up
sudo docker stop node-slave2
sudo docker rm node-slave2
------------------
=============================



*************************************************************************
