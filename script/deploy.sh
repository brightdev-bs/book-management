#!/bin/bash



BUILD_JAR=$(ls /home/ubuntu/project/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo "> JAR_NAME: ${JAR_NAME}" >> /home/ubuntu/action/deploy.log
echo "> build 파일 복사" >> /home/ubuntu/action/deploy.log

DEPLOY_PATH = /home/ubuntu/deploy
cp $BUILD_JAR $DEPLOY_PATH

echo ">> 현재 실행중인 애플리케이션 PID " >> /home/ubuntu/action/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션 없습니다." >> /home/ubuntu/action/deploy.log
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> /home/ubuntu/action/deploy.log
nohup java -jar $DEPLOY_JAR  >> /home/ec2-user/deploy.log 2>/home/ec2-user/action/deploy_err.log &
