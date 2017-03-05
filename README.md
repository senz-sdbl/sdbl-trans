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

# hosts

## epic
```
# at sdbl
10.100.31.240   8200

# public  
124.43.16.185   8200
```

## switch
```
# sdbl
10.100.31.44    7070

# local
dev.localhost   7070
172.17.0.1      7070
```
