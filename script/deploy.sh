#!/bin/bash

echo "mkdir /test"

BUILD_JAR=$(ls /home/ubuntu/project/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo "> JAR_NAME: ${JAR_NAME}"
echo "> build 파일 복사"

cp $BUILD_JAR "/home/ubuntu/deploy"

echo ">> 현재 실행중인 애플리케이션 PID "
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -9 $CURRENT_PID
  sleep 10
fi


DEPLOY_JAR="/home/ubuntu/deploy/"$JAR_NAME
chmod +x DEPLOY_JAR

echo "> DEPLOY_JAR 배포"
nohup java -jar \
    -Dspring.profiles.active=prod \
    $DEPLOY_JAR > ./nohup.out 2>&1 &