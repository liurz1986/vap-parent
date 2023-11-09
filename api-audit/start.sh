#!/bin/bash
config_server=http://vrv218:8765/info
echo "shutdown service"
docker-compose -f /data/docker/docker-compose-api.yml down
sleep 1
docker-compose -f /data/docker/docker-compose.yml down
sleep 1
echo "remove old images"
docker rmi $(docker images| grep none|awk '{print $3}')
echo "start service"
docker-compose -f /data/docker/docker-compose.yml up -d
i=0
while (( $i < 60 ))
do
	t=$(curl -s $config_server|grep {})
	i=`expr $i + 1`
	echo "waiting for config server ready ("$i")" ...
	if [ "$t" != "" ]
	then
		break
	fi
	sleep 1
done
for((j=20;j>0;j--));  
do   
sleep 1
echo "waiting for start service ("$j")" ...
done 
docker-compose -f /data/docker/docker-compose-api.yml up -d