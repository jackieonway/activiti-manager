#!/bin/bash

echo 'project is packaging ....'
cd ..
mvn clean package -Dmeven.test.skip=true
echo 'project has packaged'
cd ./bin