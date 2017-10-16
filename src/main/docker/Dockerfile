FROM lappsgrid/ubuntu
RUN apt-get update && apt-get install -y pandoc
COPY api.jar /root
CMD ["java", "-jar", "/root/api.jar"]
