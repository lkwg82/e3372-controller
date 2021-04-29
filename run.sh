#!/bin/bash

set -ex

./mvnw clean package

cd debian-package
./run_test.sh

echo " ------ "
echo " deploying "
echo " ------ "
pi=pi4_router
rsync -e ssh e3372-controller.deb $pi:
ssh $pi 'sudo dpkg -i e3372-controller.deb'
