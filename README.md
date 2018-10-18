# dockeragent

Run this container using following command :

docker run -v /var/run/docker.sock:/var/run/docker.sock -e MICRO_ENDPOINT='kafkafirslevelproduceripaddress:port' -e HOSTNAME='hostname' REDISHOST=redishostip -e REDISPORT=6379  abhisheknn/dockeragent