FROM lappsgrid/ubuntu
COPY api.jar /root
RUN apt-get update && apt-get install -y pandoc
CMD ["java", "-jar", "/root/api.jar"]
