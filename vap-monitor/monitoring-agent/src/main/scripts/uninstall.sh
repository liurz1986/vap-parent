#!/bin/bash
DIR=$(cd "$( dirname "$0")" && pwd)
echo "dir:--->$DIR"
source "${DIR}"/../common/conf.sh
echo "monitoring-agent安装卸载, 安装包目录:vap_package_dir:${vap_package_dir} , 安装至工作空间: vap_work_dir:${vap_work_dir}"
echo "vap_package_dir:${vap_package_dir}, db_host:${db_host} , db_port: ${db_port} db_pwd:${db_pwd}"

declare -x APP_NAME="monitoring-agent"

systemctl daemon-reload

#检查程序是否在运行
function is_exist(){
  pid=`ps -ef | grep ${APP_NAME} | grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
    return 1
  else
    return 0
  fi
}


function status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running. Pid is ${pid}"
  else
    echo "${APP_NAME} is NOT running."
  fi
}



#停止方法
function monitoringagentstop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
  else
    echo "${APP_NAME} is not running"
  fi
}

if [ -f /usr/lib/systemd/system/monitoring-agent.service ]; then
    monitoringagentstop 
    systemctl stop monitoring-agent
fi


echo "------------- monitoring-agent uninstall  -------------"

# 删除目录文件夹
[ -e ${vap_work_dir}/monitoring-agent ] && rm -rf ${vap_work_dir}/monitoring-agent

[ -f /usr/lib/systemd/system/monitoring-agent.service ] && rm -rf /usr/lib/systemd/system/monitoring-agent.service

[ -d "${vap_work_dir}/monitoring-agent-0.1" ] && rm -rf "${vap_work_dir}/monitoring-agent-0.1"

echo -e "\e[0;32mUninstall monitoring-agent end\e[0m"
exit 0
