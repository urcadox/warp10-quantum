#!/bin/sh

VERSION=$1

#
# Build the distribution .tgz for Warp 10 Quantum
#


ARCHIVE=./archive

# Remove existing archive dir
rm -rf ${ARCHIVE}

# Create the directory hierarchy
mkdir -p ${ARCHIVE}/bin


sed -e "s/@VERSION@/${VERSION}/g" ./src/main/sh/warp10-quantum-server.init >> ${ARCHIVE}/bin/warp10-quantum-server.init

# Copy jar
cp ./build/libs/warp10-quantum-server-${VERSION}-all.jar ${ARCHIVE}/bin/warp10-quantum-server-${VERSION}-all.jar


chmod 644 ${ARCHIVE}/bin/warp10-quantum-server-${VERSION}-all.jar
chmod 755 ${ARCHIVE}/bin/warp10-quantum-server.init

# Build tar
tar zcpf ./build/libs/warp10-${VERSION}.tar.gz ${ARCHIVE}
