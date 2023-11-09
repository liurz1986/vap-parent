FROM java:8-jre
MAINTAINER vap
VOLUME /tmp
ADD api-audit-monitor*.jar /api-audit-monitor.jar
EXPOSE 8764
ENTRYPOINT ["java", "-jar", "/api-audit-monitor.jar"]