#!/bin/bash
version = $(cat updateVersion.txt)
echo 'Update activiti manager project version to $version'
cd ..
mvn versions:set -DnewVersion=$version
echo 'Update activiti manager version to $version success'
echo 'Update activiti manager child modules version to $version'
mvn versions:update-child-modules
echo 'Update activiti manager child modules version to $version success'