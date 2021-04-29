#!/bin/bash

set -euo pipefail
shopt -s expand_aliases

cp -v ../target/e3372-controller-jar-with-dependencies.jar .

# build build image
docker build -t build -f Docker_build .
docker run --rm -ti -v "$PWD:/out" -w /out --user "$(id -u)" \
  build \
  dpkg-deb --build /src e3372-controller.deb

## test images
#docker build -t test_debian_10 -f Docker_debian_10 .
#
#function run_debian_10() {
#  docker run --rm -ti -v "$PWD:/tmp" -w /tmp \
#    test_debian_10 \
#    "$@"
#}
#
#run_debian_10 dpkg -i e3372-controller.deb

dpkg -c e3372-controller*deb

function check_file_in_deb() {
  local pattern=$1
  echo -n "check ... $pattern "
  if dpkg -c e3372-controller*deb | grep "$pattern"; then
    echo "ok"
  else
    echo "fail"
    exit 1
  fi
}

check_file_in_deb opt/e3372-controller/e3372-controller-jar-with-dependencies.jar

vagrant up
vagrant upload e3372-controller.deb
vagrant ssh -c 'sudo dpkg -i e3372-controller.deb || sudo apt-get install -f -y'
vagrant ssh -c 'sudo dpkg -i e3372-controller.deb'
vagrant ssh -c 'sudo shutdown -r now & exit'
sleep 10
vagrant ssh -c 'sudo systemctl status e3372-controller'
