FROM docker.io/java:8-jdk
# FROM docker.io/java:8-jre
# FROM docker.io/java:8-jre-alpine
# FROM docker.io/java:8-jdk-alpine
# FROM docker.io/java:9
# MAINTAINER tangfeixiong <tangfx128@gmail.com>
# Inspired by: Jorge Morales <jmorales@redhat.com>
# and Jakub Hadvig <jhadvig@redhat.com> (https://github.com/sclorg/s2i-base-container)
# and [Oracle Java repository](https://github.com/oracle/docker-images)
LABEL maintainer "tangfeixiong <tangfx128@gmail.com>"

# server JRE 7 "http://download.oracle.com/otn-pub/java/jdk/7u80-b15/server-jre-7u80-linux-x64.tar.gz"
# server JRE 8 "http://download.oracle.com/otn-pub/java/jdk/8u112-b15/server-jre-8u112-linux-x64.tar.gz"
# Java download location (default is Oracle Server JRE 8)
ARG java_pkg="http://172.17.4.1:48080/99-mirror/http%3A%2F%2Fdownload.oracle.com/otn-pub%2Fjava%2Fjdk%2F8u112-b15/server-jre-8u112-linux-x64.tar.gz"

COPY /sm-crypto.jar /opt/ecp/app.jar
    
RUN set -x \
    temp_dir=/tmp/build \
# Install Glusterfs client package
    && wget -O - http://download.gluster.org/pub/gluster/glusterfs/LATEST/rsa.pub | apt-key add - \
    && DEBID=$(grep 'VERSION_ID=' /etc/os-release | cut -d '=' -f 2 | tr -d '"') \
    && DEBVER=$(grep 'VERSION=' /etc/os-release | grep -Eo '[a-z]+') \
    && echo deb https://download.gluster.org/pub/gluster/glusterfs/LATEST/Debian/${DEBID}/apt ${DEBVER} main > /etc/apt/sources.list.d/gluster.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends glusterfs-api glusterfs-client \
    && rm -rf /var/lib/apt/lists/* \
# Finally
    && rm -rf $temp_dir \
    && /bin/true

ENV JAVA_OPTIONS="-Xms128m -Xmx512m -XX:PermSize=64m -XX:MaxPermSize=256m" \
    SERVER_PORT="9099" \
    SPRING_HTTP_MULTIPART_MAX_FILE_SIZE=3000MB \
    SPRING_HTTP_MULTIPART_MAX_REQUEST_SIZE=30GB \
    GLUSTERFS_SERVER="172.17.4.200" \
    GLUSTERFS_VOLUME="glustervol1" \
    STORAGE_LOCATION="ecp"
    
EXPOSE 9099

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Xms128m","-Xmx512m","-XX:PermSize=64m","-XX:MaxPermSize=256m","-jar","/opt/ecp/app.jar"]