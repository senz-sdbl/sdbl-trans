# Build docker images
```
sbt assembly
docker build -t senz/sdbl-trans .
```

# Run with docker
```
docker run -it \
-e SWITCH_HOST=dev.localhost \
-e SWITCH_PORT=7070 \
-e EPIC_HOST=220.247.245.88 \
-e EPIC_PORT=8200 \
-e CASSANDRA_HOST=10.2.2.23 \
-e CASSANDRA_PORT=9042 \
-e MYSQL_HOST=dev.localhost \
-e MYSQL_PORT=3306 \
-v /home/docker/sdbl/logs:/app/logs:rw \
-v /home/docker/sdbl/keys:/app/.keys:rw \
erangaeb/sdbltrans:0.1
```