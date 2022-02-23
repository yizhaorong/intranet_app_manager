#!/bin/sh
dir=$(pwd)
configPath=$dir/src/main/resources/application.properties
caPath=$dir/src/main/resources/static/crt/ca.crt
p12Path=$dir/src/main/resources/server.pkcs12
sslConfigPath=$dir/openssl.cnf
param=$1

echo "$param"
getIpForMac(){
  ifconfig | grep inet | grep -v inet6 | grep -v 127 | cut -d ' ' -f2
}

getIpForLinux(){
  ip a | grep inet | grep -v inet6 | grep -v 127 | sed 's/^[ \t]*//g' | cut -d ' ' -f2
}

ip="127.0.0.1"
if [[ $(uname  -a) =~ "Darwin" ]];then
    ip=$(getIpForMac)
elif [[ $(uname  -a) =~ "centos" ]];then
    ip=$(getIpForLinux)
elif [[ $(uname  -a) =~ "ubuntu" ]];then
    ip=$(getIpForLinux)
fi



build(){
  mysqlAddr='127.0.0.1'
  if [[ $param =~ "docker" ]];then
    mysqlAddr="docker_mysql"
  fi
  sed -i "" "s/\${ip}/$mysqlAddr/" "$configPath"
  gradle clean
  gradle build
  cd "$dir/build/libs/"
  mv intranet_app_manager*.jar intranet_app_manager.jar
  cd "$dir"
  sed -i "" "s/$mysqlAddr/\${ip}/" "$configPath"
}

createCert(){
  cd "$dir"
  rm -rf certs
  mkdir -p certs/CA/{certs,crl,newcerts,private}
  touch certs/CA/index.txt
  touch certs/CA/certs.db
  cp -rf "$sslConfigPath" certs/openssl.cnf
  echo 00 > certs/CA/serial
  sed -i "" "s/\${ip}/$ip/" "$dir/certs/openssl.cnf"
  cd "$dir/certs"
  echo "输入:123456"
  openssl req -new -x509 -days 3650 -keyout ca.key -out ca.crt -config openssl.cnf
  openssl genrsa -out server.key 2048
  openssl req -new -key server.key -out server.csr -config openssl.cnf
  openssl ca -in server.csr -out server.crt -cert ca.crt -keyfile ca.key -extensions v3_req -config openssl.cnf
  openssl pkcs12 -export -in server.crt -inkey server.key -out server.pkcs12
  cp -rf "$dir/certs/ca.crt" "$caPath"
  cp -rf "$dir/certs/server.pkcs12" "$p12Path"
  cd "$dir"
  rm -rf certs
}

installAndStartMysql(){
  brew install mysql
  killall -9 mysqld
  mysqld &
  mysql -u root -p <"$dir/mysql/sql/init.sql"
}

startup(){
  ps -efww | grep -w 'intranet_app_manager' | grep -v grep |awk '{print $2}'|xargs kill -9
  if [[ $param =~ "docker" ]];then
    killall -9 mysqld
    docker-compose up -d
  else
    cd "$dir/build/libs"
    java -jar intranet_app_manager.jar &
  fi

}

openPage(){
  address="http://$ip:8080/account/signin"
  echo "$address"
  if [[ $(uname  -a) =~ "Darwin" ]];then
      open "$address"
  elif [[ $(uname  -a) =~ "centos" ]];then
      x-www-browser "$address"
  elif [[ $(uname  -a) =~ "ubuntu" ]];then
      x-www-browser "$address"
  fi
}

setup(){
  createCert
  build
  if [[ $param =~ "docker" ]];then
    docker-compose build
  else
    installAndStartMysql
  fi
  startup
  openPage
}

setup