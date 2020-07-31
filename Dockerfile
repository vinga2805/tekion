FROM tomcat:jdk8-adoptopenjdk-openj9
LABEL maintainer="vishalonwork@gmail.com"
ADD target/teslaDyDB-0.0.1-SNAPSHOT.war  /usr/local/tomcat/webapps/teslaDyDB.war
RUN mkdir -p /var/log/apps/teslaDyDB
WORKDIR /usr/local/tomcat/bin/
RUN curl https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.13.0/jmx_prometheus_javaagent-0.13.0.jar -o /usr/local/tomcat/bin/jmx_prometheus_javaagent.jar -L
ADD prometheus-jmx-config.yaml /usr/local/tomcat/bin/
ADD catalina.sh /usr/local/tomcat/bin/
RUN chmod +x /usr/local/tomcat/bin/catalina.sh
EXPOSE 8080
CMD ["catalina.sh", "run"]
