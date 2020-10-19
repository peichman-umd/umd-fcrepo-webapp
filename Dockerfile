FROM maven AS compile

ENV SOURCE_DIR /opt/umd-fcrepo-webapp
COPY src $SOURCE_DIR/src
COPY pom.xml $SOURCE_DIR
WORKDIR $SOURCE_DIR
RUN mvn package -DwarFileName=umd-fcrepo-webapp

FROM tomcat:7

COPY --from=compile /opt/umd-fcrepo-webapp/target/umd-fcrepo-webapp.war /usr/local/tomcat/webapps/ROOT.war
COPY setenv.sh /usr/local/tomcat/bin/

VOLUME /var/umd-fcrepo-webapp