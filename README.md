# Build docker images
```
sbt assembly
docker build -t senz/sdbl-trans .
```

# Run with docker
```
docker run -it \
-e SWITCH_HOST=10.100.31.34 \
-e SWITCH_PORT=9090 \
-e EPIC_HOST=10.100.31.240 \
-e EPIC_PORT=8200 \
-e CASSANDRA_HOST=10.100.31.34 \
-e CASSANDRA_PORT=9042 \
-e MYSQL_HOST=dev.localhost \
-e MYSQL_PORT=3306 \
-v /home/docker/sdbl/logs:/app/logs:rw \
-v /home/docker/sdbl/keys:/app/.keys:rw \
erangaeb/sdbltrans:0.1
```