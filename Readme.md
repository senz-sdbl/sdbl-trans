# Build docker images
1. sbt assembly
2. docker build -t senz/sdbl-trans .

# Run with docker
docker run -it \
-e SWITCH_HOST=dev.localhost \
-e SWITCH_PORT=9090 \
-e EPIC_HOST=220.247.245.88 \
-e EPIC_PORT=8200 \
-e CASSANDRA_HOST=10.2.2.23 \
-v /Users/eranga/sdbl/trans/logs:/app/logs:rw \
-v /Users/eranga/sdbl/trans/.keys:/app/.keys:rw \
senz/sdbl-trans