#!/bin/bash
DIR=$(cd "$(dirname "$0")" && pwd)
echo "$DIR"
APP_NAME="monitoring-agent"
echo "APP_NAME=$APP_NAME"
source /etc/profile

#使用说明，用来提示输入参数
usage() {
    echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
    exit 1
}
#检查程序是否在运行
is_exist(){
  pid=`ps -ef | grep java |grep ${DIR}/target/${APP_NAME}.jar |grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#<==限制内存大小


start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "Service : $APP_NAME is already running. pid=${pid} ."
  else
    echo "start $APP_NAME"
    nohup java -jar -Xms200m -Xmx200m  ${DIR}/target/${APP_NAME}.jar  --spring.profiles.active=pro --spring.config.location=${DIR}/conf/application.yml >> ${DIR}/logs/monitoring-agent.log 2>1&
    echo "查看状态: systemctl status $APP_NAME -l"
    echo "查看详细日志: tail -300f ${DIR}/logs/${APP_NAME}.log"
  fi
}

#停止方法
stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
  else
    echo "Service $APP_NAME is not running"
  fi
}

#输出运行状态
status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "Service $APP_NAME is running. Pid is ${pid}"
  else
    echo "Service $APP_NAME is NOT running."
  fi
}

#重启
restart(){
  stop
  start
}

#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac
