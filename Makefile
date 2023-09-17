VERSION := $(shell deployment/scripts/version.sh)
DOCKER_COMPOSE_FILE := deployment/docker-compose.yaml

<<<<<<< HEAD
libraries:
	rm -rf package/mof
	mkdir -p package/mof
	cp logging.properties package/mof
	find sdk/sdk-package/src/main/resources/ -type f | grep -i properties$ | xargs -i cp {} package/mof
	cp pom.xml package/mof
	cp -r core package/mof
	cp -r parent package/mof
	cp -r mission package/mof
	cp -r sdk package/mof
	cd package && docker buildx build --no-cache --platform linux/arm64/v8 -f Dockerfile.Libraries -t ghcr.io/kosmoedge/nmf-libraries:latest . --load
	rm -rf package/mof	
=======
assembly-jar:
	mvn clean install -P assembly-with-dependencies
>>>>>>> b00066dd (Add build to jar)

supervisor: 
	docker buildx build --no-cache --platform linux/arm64/v8 --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.Supervisor -t ghcr.io/kosmoedge/supervisor:latest . --load

<<<<<<< HEAD
supervisor: 
	cd package && docker buildx build --build-arg VERSION=${VERSION} --no-cache --platform linux/arm64/v8 -f Dockerfile.Simulator -t ghcr.io/kosmoedge/supervisor:latest . --load
=======
space-module-%: 
	docker buildx build --no-cache --platform linux/arm64/v8 --build-arg MODULE_PATH=sdk/examples/space/$* --build-arg MODULE_NAME=$* --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.SpaceModule -t ghcr.io/kosmoedge/$*:latest . --load

ground-module-%: 
	docker buildx build --no-cache --build-arg MODULE_PATH=sdk/examples/ground/$* --build-arg MODULE_NAME=$* --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.GroundModule -t ghcr.io/kosmoedge/$*:latest .
>>>>>>> b00066dd (Add build to jar)

consumer-tool:
	docker buildx build --build-arg VERSION=$(VERSION) -f deployment/Dockerfile.ConsumerTool -t ghcr.io/kosmoedge/nmf-consumer-tool:latest .

containers: assembly-jar supervisor consumer-tool

run: containers
	mkdir -p ~/.m2/int/esa
	docker run -d --rm --name consumer-tool-temp kosmoedge/nmf-consumer-tool:latest 
	# docker cp consumer-tool-temp:/root/.m2/repository/int/esa ~/.m2/repository/int
	docker cp /mof/sdk/sdk-package/target/nmf-sdk-$(VERSION) ~/.m2/repository/int
	docker stop consumer-tool-temp

create_docker_net:
	@ docker network inspect mo-bridge > /dev/null 2> /dev/null && : || docker network create mo-bridge

start: create_docker_net
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) up -d $(CONTAINER_NAMES)

# stop all containers or specific ones
stop:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) stop $(CONTAINER_NAMES)

# stop all containers and remove built images
down:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) down --rmi local

# restart all or specific containers
restart: stop start
