ROOT=$(shell pwd)
JAR=api.jar
CSS=src/main/resources/static/style/main.css

jar:
	mvn compile
	mvn package

clean:
	mvn clean
	#[[ -f $(CSS) ]] && rm $(CSS)

less:
	lessc src/main/less/main.less $(CSS)

docker:
	cp target/api.jar src/main/docker
	cd src/main/docker && docker build -t lappsgrid/api-service .

run:
	java -jar target/$(JAR)
	
start:
	docker run -d -p 8080:8080 -v /private/etc/lapps:/etc/lapps --name api lappsgrid/api-service

stop:
	docker rm -f api

push:
	docker push lappsgrid/api-service

deploy:
	os ssh services /root/services.sh update api

all: clean jar docker push deploy

run:
	java -jar target/api.jar


