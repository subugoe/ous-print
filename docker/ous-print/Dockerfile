# syntax=docker/dockerfile:experimental

FROM maven:3.6.2-ibmjava-8-alpine

LABEL maintainer="mahnke@sub.uni-goettingen.de"

ENV BUILD_CONTEXT=/mnt/build-context \
    BUILD_DIR=/tmp/ous-print \
    INSTALL_DIR=/opt/ous-print

RUN --mount=target=/mnt/build-context mkdir $BUILD_DIR $INSTALL_DIR && \
    cp -r $BUILD_CONTEXT/* $BUILD_DIR/ && \
# Install dependencies
    apk --update upgrade && \
    apk add --no-cache $REQ_BUILD busybox && \
    cd $BUILD_DIR/ && \
    mvn install -pl layout-test && \
    mvn compile # package

# TODO: Install artifacts to /opt/

# RUN rm -rf $BUILD_DIR /root/.m2