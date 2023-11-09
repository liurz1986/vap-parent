#!/bin/bash
source /etc/profile
DIR=$(cd "$( dirname "$0")" && pwd)
echo "dir:--->$DIR"
source "$DIR"/../common/conf.sh
echo "monitoring-agent安装开始, 安装包目录:vap_package_dir:${vap_package_dir} , 安装至工作空间: vap_work_dir:${vap_work_dir}"
echo "vap_package_dir:${vap_package_dir}, db_host:${db_host} , db_port: ${db_port}, db_pwd:${db_pwd}"
echo "-----安装方式:${auto_type}------ 0:自动安装   1:解压手动安装 -- monitoring-agent install start -------------"


function copyR(){
  # 拷贝文件到新目录空间
  cp -rp ${vap_package_dir}/monitoring-agent/package/monitoring-agent-0.1 ${vap_work_dir}/
  # 增加软连接
  [ -d ${vap_work_dir}/monitoring-agent ] && rm -rf ${vap_work_dir}/monitoring-agent
  [ -L ${vap_work_dir}/monitoring-agent ] && rm -rf ${vap_work_dir}/monitoring-agent
  ln -s ${vap_work_dir}/monitoring-agent-0.1  ${vap_work_dir}/monitoring-agent
  
  # 新目录空间中配置文件修改
  #cp ${vap_package_dir}/monitoring-agent/conf/application.properties ${vap_work_dir}/monitoring-agent/conf/
  # 执行脚本
  \cp -rp ${vap_package_dir}/monitoring-agent/startup.sh ${vap_work_dir}/monitoring-agent
}

function chmodR(){
  # 赋值权限
  chmod +x ${vap_work_dir}/monitoring-agent/*.sh
}

function configR(){
  # 自启动配置修改
  \cp -rf ${vap_package_dir}/monitoring-agent/conf/monitoring-agent.service  /usr/lib/systemd/system
  sed -i "s#/opt/vrv/vap/vap-install#${vap_work_dir}#g" /usr/lib/systemd/system/monitoring-agent.service
  # 修改配置连接mysql配置信息
  #sed -i "s/^db\.password.*/db\.password=$db_pwd/g"  ${vap_work_dir}/monitoring-agent/conf/application.properties
  # 删除指定的行
  sed -i "/^Environment=.*/d" /usr/lib/systemd/system/monitoring-agent.service
  # 指定行插入
  echo "自定义javahome:"$JAVA_HOME
  local_jdk_path=`which java`
  echo "local_jdk_path:$local_jdk_path"
  if [ -n "${local_jdk_path}" ]; then
     # 判断某链接是否存在
    if [ -L "${local_jdk_path}" ]; then 
      echo "Link exist ---> ${local_jdk_path}"
      local real_jdk_path=`readlink -f ${local_jdk_path}`
      echo "---->----real_jdk_path:$real_jdk_path"
      local local_java=`echo "$real_jdk_path" | sed 's#/bin/java##g'`
      echo "local_java=$local_java"
      sed -i "6 i Environment=\"JAVA_HOME=$(echo $local_java)\"" /usr/lib/systemd/system/monitoring-agent.service
    else
      echo "Link doesn't exist  ---> ${local_jdk_path}"
      sed -i "6 i Environment=\"JAVA_HOME=$(echo $JAVA_HOME)\"" /usr/lib/systemd/system/monitoring-agent.service
    fi
  else
    echo "已经设置的JAVA_HOME=$JAVA_HOME"
    sed -i "6 i Environment=\"JAVA_HOME=$(echo $JAVA_HOME)\"" /usr/lib/systemd/system/monitoring-agent.service
  fi
}

function systemctldR(){
  # 启动服务
  systemctl daemon-reload
  systemctl enable monitoring-agent
  systemctl start monitoring-agent
  systemctl status monitoring-agent
}

function installR(){
  if [ '0' != $auto_type ] ; then
    copyR
    chmodR
  fi
  configR
  systemctldR
}
installR

# 判断启动成功
temp=`systemctl status monitoring-agent | grep running`
if [ ! -z "$temp" ]
  then
    echo -e '\033[32m monitoring-agent安装启动成功 \033[0m'
    exit 0
else
    echo -e '\033[31m monitoring-agent安装启动失败 \033[0m'
   exit 1
fi
