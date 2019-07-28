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
	os ssh services /root/services.sh update api

all: clean less jar docker 




