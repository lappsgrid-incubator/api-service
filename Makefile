ROOT=$(shell pwd)
JAR=api.jar
CSS=src/main/resources/static/style/main.css
REPO=docker.lappsgrid.org
GROUP=lappsgrid
NAME=api-service
IMAGE=$(GROUP)/$(NAME)
TARGET=target/$(JAR)
TAG=$(REPO)/$(IMAGE)

jar:
	mvn compile
	mvn package

clean:
	mvn clean
	#[[ -f $(CSS) ]] && rm $(CSS)

less:
	lessc src/main/less/main.less $(CSS)

docker:
	if [ ! -e src/main/docker/$(JAR) ] ; then cp $(TARGET) src/main/docker ; fi
	if [ $(TARGET) -nt src/main/docker/$(JAR) ] ; then cp $(TARGET) src/main/docker ; fi
	cd src/main/docker && docker build -t $(IMAGE) .

run:
	java -jar $(TARGET)
	
start:
	docker run -d -p 8080:8080 -v /private/etc/lapps:/etc/lapps --name api $(IMAGE)

stop:
	docker rm -f api

push:
	docker tag $(IMAGE) $(TAG)
	docker push $(TAG)

deploy: 
	os ssh 149.165.157.51 root /root/services.sh update api

all: clean less jar docker 

help:
	@echo "\nGOALS\n"
	@echo "    jar    - generates an executable .jar file"
	@echo "    less   - generates main.css from the main.less"
	@echo "    clean  - removes build artifacts"
	@echo "    docker - create the Docker image"
	@echo "    run    - runs the jar file"
	@echo "    start  - starts a Docker container"
	@echo "    stop   - stops a running Docker container"
	@echo "    push   - tags and pushes the Docker container to docker.lappsgrid.org"
	@echo "    deploy - invokes the update script on the services instance."
	@echo "    all    - cleans and builds the Docker container"
	@echo "    help   - prints this help message.\n"



