FROM jenkins/jenkins:2.303.2-lts

USER root
RUn set -x \
    && apt update \
    && apt install apt-transport-https ca-certificates curl gnupg2 software-properties-common --yes \
    && curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - \
    && add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" \
    && apt update \
    && apt install docker-ce --yes \
    && usermod -G docker jenkins

RUN set -x \
    && apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 93C4A3FD7BB9C367 \
    && apt update \
    && apt install ansible --yes

USER jenkins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY jcasc/jenkins.yml /usr/share/jenkins/ref/jenkins.yml
RUN set -x \
    && mkdir -p /tmp/shared-lib \
    && /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
