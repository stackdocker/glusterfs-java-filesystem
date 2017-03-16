FROM docker.io/centos:centos7
# MAINTAINER tangfeixiong <tangfx128@gmail.com>
# Inspired by: Jorge Morales <jmorales@redhat.com>
# and Jakub Hadvig <jhadvig@redhat.com> (https://github.com/sclorg/s2i-base-container)
# and [Oracle Java repository](https://github.com/oracle/docker-images)
LABEL maintainer "tangfeixiong <tangfx128@gmail.com>"

# server JRE 7 "http://download.oracle.com/otn-pub/java/jdk/7u80-b15/server-jre-7u80-linux-x64.tar.gz"
# server JRE 8 "http://download.oracle.com/otn-pub/java/jdk/8u112-b15/server-jre-8u112-linux-x64.tar.gz"
# Java download location (default is Oracle Server JRE 8)
ARG java_pkg="http://172.17.4.1:48080/99-mirror/http%3A%2F%2Fdownload.oracle.com/otn-pub%2Fjava%2Fjdk%2F8u112-b15/server-jre-8u112-linux-x64.tar.gz"

COPY /maven/multipart-upload.jar /opt/ecp/app.jar

ENV JAVA_HOME="/opt/java" \
    PATH=$PATH:/opt/java/bin 
    
RUN set -x \
    temp_dir=/tmp/build \
# Install Oracle Server JRE package
    && mkdir -p $JAVA_HOME \
    && javaPkgSrc="${java_pkg:-'http://download.oracle.com/otn-pub/java/jdk/8u112-b15/server-jre-8u112-linux-x64.tar.gz'}" \
    && if [[ -f $temp_dir/$javaPkgSrc ]]; then \
        tar -C $JAVA_HOME --strip-components=1 -zxf $temp_dir/$javaPkgSrc; \
        rm -f $temp_dir/$javaPkgSrc; \
    elif [[ ! "$javaPkgSrc" =~ https?://download\.oracle\.com/?.* ]]; then \
        curl -jkSL ${javaPkgSrc//%/%25} \
            | gunzip -c - \
            | tar -x -C $JAVA_HOME --strip-components=1; \
    else \
        curl -jkSLH "Cookie: oraclelicense=accept-securebackup-cookie; " "$javaPkgSrc" \
            | gunzip \
            | tar -x -C "$JAVA_HOME" --strip-components=1; \
    fi \
# Install Glusterfs39
    && yum install -y centos-release-gluster39 \
    && yum install -y glusterfs-api glusterfs-client \
    && yum clean all \
# Finally
    && rm -rf $temp_dir \
    && echo

ENV JAVA_OPTIONS="-Xms128m -Xmx512m -XX:PermSize=64m -XX:MaxPermSize=256m" \
    SERVER_PORT="9099" \
    SPRING_HTTP_MULTIPART_MAX_FILE_SIZE=3000MB \
    SPRING_HTTP_MULTIPART_MAX_REQUEST_SIZE=30GB \
    GLUSTERFS_SERVER="172.17.4.200" \
    GLUSTERFS_VOLUME="glustervol1" \
    STORAGE_LOCATION="ecp"
    
EXPOSE 9099

CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Xms128m","-Xmx512m","-XX:PermSize=64m","-XX:MaxPermSize=256m","-jar","/opt/ecp/app.jar"]