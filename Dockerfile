FROM ubuntu:22.04

LABEL org.opencontainers.image.source="https://github.com/sindram/solar-proxy-tester"

RUN apt-get update && \
    apt-get install -y openjdk-11-jdk-headless curl grep sed vim nano openssl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

WORKDIR /solar-proxy-tester

COPY build/libs /solar-proxy-tester/libs/

RUN echo "alias ll='ls -la'" >> /etc/bash.bashrc && \
    echo "alias ll='ls -la'" >> /root/.bashrc

RUN echo "alias ..='cd ..'" >> /etc/bash.bashrc && \
    echo "alias ..='cd ..'" >> /root/.bashrc

CMD ["/bin/bash", "-c", "source /etc/bash.bashrc && source /root/.bashrc && tail -f /dev/null"]

