FROM jenkins/jenkins:2.303.2-lts
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY jcasc/jenkins.yml /usr/share/jenkins/ref/jenkins.yml
RUN set -x \
    && mkdir -p /tmp/shared-lib \
    && /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
EXPOSE 8080
