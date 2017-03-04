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
-e EPIC_HOST=124.43.16.185 \
-e EPIC_PORT=8200 \
-e MYSQL_HOST=dev.localhost \
-e MYSQL_PORT=3306 \
-v /home/docker/sdbl/trans/logs:/app/logs:rw \
-v /home/docker/sdbl/trans/keys:/app/.keys:rw \
erangaeb/sdbltrans:0.5
```