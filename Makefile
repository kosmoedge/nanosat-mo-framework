VERSION := $(shell deployment/scripts/version.sh)
DOCKER_COMPOSE_FILE := deployment/docker-compose.yaml


#---- Build & push service images ----#
# Build & push Supervisor image
supervisor: 
	docker buildx build --no-cache --platform linux/arm64/v8 --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.Supervisor -t ghcr.io/kosmoedge/supervisor:latest . --load

# Build & push space module image
space-module-%: 
	docker buildx build --no-cache --platform linux/arm64/v8 --build-arg MODULE_PATH=sdk/examples/space/$* --build-arg MODULE_NAME=$* --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.SpaceModule -t ghcr.io/kosmoedge/$*:latest . --load

# Build & push ground module image
ground-module-%: 
	docker buildx build --no-cache --build-arg MODULE_PATH=sdk/examples/ground/$* --build-arg MODULE_NAME=$* --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.GroundModule -t ghcr.io/kosmoedge/$*:latest .


#---- Helper operations ----#
assembly-jar:
	mvn clean install -P assembly-with-dependencies

consumer-tool:
	mvn clean install && sdk/sdk-package/target/nmf-sdk-$(VERSION)/home/nmf/consumer-test-tool/consumer-test-tool.sh


#---- Run containers using docker compose --
# Setup network
create_docker_net:
	@ docker network inspect mo-bridge > /dev/null 2> /dev/null && : || docker network create mo-bridge

# Start all or specific containers
start: create_docker_net
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) up -d $(CONTAINER_NAMES)

# Stop all or specific containers
stop:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) stop $(CONTAINER_NAMES)

# Stop all containers and remove built images
down:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) down --rmi local

# Restart all or specific containers
restart: stop start
