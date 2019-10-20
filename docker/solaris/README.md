Experimental Docker image for Solaris 10 SPARC
==============================================

# Introduction

This project aimes to create a emulation of a LBS deployment environment.

## Status

Currently Solaris 10 will end up in a panic during the boot process.
So this isn't working at all at the current moment. 

# Building

## Getting a Solaris installation DVD image

### Solaris 8

Due to licensing issues you need to provide a Solaris 8 CD ISO images yourself. You can download the two part 
of the image frim [this site](http://ftp.icm.edu.pl/packages/solaris-cd-pm/8/sparc/). You need

### Solaris 10

Due to licensing issues you need to provide a Solaris 10 DVD ISO image yourself. You might to sign up for a Oracle 
Account to download the image. You can download the image on a [Oracle web site](https://www.oracle.com/solaris/solaris10/downloads/solaris10-get-jsp-downloads.html).

## Running `docker build` 

Place the downloaded file next to the `Dockefile` and make sure the variable `ISO` matches the file name of the `.iso` file.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
DOCKER_BUILDKIT=1 docker build .
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Starting

## Get a command shell

Use the hash of the image build by the previous command and pass it to docker run

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
docker run -it HASH /bin/sh
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

## Running QEMU with Solaris 10 Installation CD

This starts Solaris 8 on a 32 Bit emulation.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
/opt/qemu/bin/qemu-system-sparc -boot d -cdrom /tmp/build/sol-8-u7-install-sparc.iso  -m 256 -nographic
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

## Running QEMU with Solaris 10 Installation CD

This doesn't work yet!

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
/opt/qemu/bin/qemu-system-sparc64 -drive file=$SOLARIS_DIR/solaris.qcow2,if=ide,bus=0,unit=0 \
  -drive file=/tmp/build/sol-8-u7-install-sparc.iso,format=raw,if=ide,bus=1,unit=0,media=cdrom,readonly=on \
  -boot d -m 1536 -qmp tcp:localhost:4444,server,nowait -nographic
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Notes

## TODO

  - Try Solaris 8 - this should work better - [Download site](http://ftp.icm.edu.pl/packages/solaris-cd-pm/8/sparc/)

## Useful links

  - [QEMU Machine Protocol](https://wiki.qemu.org/QMP) - might be useful to script QEMU during the build process
  
  - [QEMU Docker](https://github.com/joshkunz/qemu-docker) - Example QEMU project with device sharing
  
  - [QEMU SPARC](https://wiki.qemu.org/Documentation/Platforms/SPARC) - Compatibility of the SPARC emulation
  
  - [Solaris 2.6 on QEMU](https://brezular.com/2012/02/17/installation-solaris-2-6-sparc-on-qemu-part2-solaris-installation/)
  
  - [Solaris unattended installation](https://projects.theforeman.org/projects/foreman/wiki/Solaris_Unattended_installation)