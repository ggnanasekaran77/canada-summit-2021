FROM jenkins/jenkins:2.289.3-lts

ADD src/ /usr/share/jenkins/ref/init.groovy.d/
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY jcasc/jenkins.yml /tmp/
RUN set -x \
    && /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
