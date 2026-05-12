#!/usr/bin/env bash
set -e

VERSION=$@
if [ -z "$VERSION" ]
  then
    echo "Usage:"
    echo "  make-release.sh VERSION"
    exit -1
fi

./gradlew :installDist
pushd build/install
zip -r "../../releases/vidette-$VERSION.zip"  vidette
popd
