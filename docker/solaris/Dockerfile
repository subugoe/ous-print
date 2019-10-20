# syntax=docker/dockerfile:experimental

FROM alpine:3.10

LABEL maintainer="mahnke@sub.uni-goettingen.de"

ENV BUILD_CONTEXT=/mnt/build-context \
    ISO="sol-8*" \
    BUILD_DIR=/tmp/build \
    REQ_BUILD="gcc libc-dev glib-dev alpine-sdk make linux-headers wget tar xz python pixman-dev pkgconf" \
    REQ_RUN="pixman glib libgcc" \
    QEMU_FILE=qemu-4.1.0.tar.xz  \
    TARGETS="sparc-softmmu,sparc64-softmmu" \
    IMAGE_SIZE="5G" \
    USER=solaris \
    INSTALL_DIR=/opt/qemu \
    HOME_DIR=/opt/solaris
    
    
# sparc-linux-user,sparc32plus-linux-user,sparc64-linux-user
RUN --mount=target=/mnt/build-context mkdir $BUILD_DIR $INSTALL_DIR $HOME_DIR && \
# Install dependencies
    apk --update upgrade && \
    apk add --no-cache $REQ_RUN $REQ_BUILD busybox && \
# Setup installation directory and user
    addgroup -Sg 1000 $USER && \
    adduser -SG $USER -u 1000 -h $HOME_DIR $USER &&\
# Start buildig
    cd $BUILD_DIR && \
    wget https://download.qemu.org/$QEMU_FILE && \
    tar xvJf $QEMU_FILE && \
    cd $BUILD_DIR/$(basename $QEMU_FILE .tar.xz) && \
	./configure --help && \
	./configure --disable-xen --disable-bluez --disable-kvm --disable-kvm --disable-kvm --disable-opengl \
	            --disable-glusterfs --disable-rdma --disable-virglrenderer --disable-dmg --target-list="$TARGETS" \
	            --disable-libusb --disable-usb-redir --disable-gtk --disable-seccomp --disable-smartcard \
	            --disable-debug-info --disable-debug-tcg --disable-pie --disable-parallels --disable-vxhs \
	            --disable-bochs --disable-replication --disable-bzip2 --disable-lzfse --disable-rbd --disable-mpath \
	            --disable-docs --disable-sdl --disable-sdl-image --disable-numa --disable-snappy --disable-sheepdog \
	            --disable-vte --disable-brlapi --disable-tpm --disable-xfsctl --disable-whpx --disable-pvrdma \
	            --disable-live-block-migration --prefix=$INSTALL_DIR && \
# --static 
    cd $BUILD_DIR/$(basename $QEMU_FILE .tar.xz) && \ 
    make install && \
	cd / && \
# Remove $BUILD_DIR and reuse it for installation
	rm -rf $BUILD_DIR/* && \
    apk del $REQ_BUILD && \
    cp $BUILD_CONTEXT/$ISO $BUILD_DIR && \
    $INSTALL_DIR/bin/qemu-img create -f qcow2 $SOLARIS_DIR/solaris.qcow2 $IMAGE_SIZE && \
    chown -R $USER:$USER $HOME_DIR


#    /usr/local/bin/qemu-system-sparc64 -boot d -cdrom /tmp/$ISO -m 512 -hda solaris.qcow2
#RUN /usr/local/bin/qemu-system-sparc64 -boot d -cdrom /tmp/$ISO -m 512
# -m 1536 -qmp tcp:localhost:4444,server,nowait -nographic

#qemu-system-sparc64 \
#  -drive file=solaris.qcow2,if=ide,bus=0,unit=0 \
#  -drive file=$ISO,format=raw,if=ide,bus=1,unit=0,media=cdrom,readonly=on \
#  -boot d -m 1536 -qmp tcp:localhost:4444,server,nowait -nographic
  


EXPOSE 5900