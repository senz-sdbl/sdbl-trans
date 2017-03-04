# Build docker images
```
sbt assembly
docker build -t senz/sdbltrans:0.4 .
```

# Run with docker
```
docker run -it -d \
-e SWITCH_HOST=dev.localhost \
-e SWITCH_PORT=7070 \
-e EPIC_HOST=52.5.201.100 \
-e EPIC_PORT=7070 \
-e CASSANDRA_HOST=10.100.31.34 \
-e CASSANDRA_PORT=9042 \
-e MYSQL_HOST=dev.localhost \
-e MYSQL_PORT=3306 \
-v /home/docker/sdbl/logs:/app/logs:rw \
-v /home/docker/sdbl/keys:/app/.keys:rw \
erangaeb/sdbltrans:0.4
```