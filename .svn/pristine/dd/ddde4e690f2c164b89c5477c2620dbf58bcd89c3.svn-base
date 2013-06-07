#!bin/bash

usage()
{
    echo "Usage: `basename $0` [-s] "
    exit 1;
}

# check parameters
while getopts "s" Option
do
  case $Option in
      s ) echo "XCS_REGISTER_SKIP = true : skip xcs registry"
          XCS_REGISTER_SKIP=true;;
      * ) usage
  esac
done

test1=`echo $0 | awk -F/ 'BEGIN { ORS="/"; }  \
       { i = 1; while ( i < NF ) { print $i; i++;}}'`
#echo $test1

if [ -z "$test1" ];
then
    xoapidFile="./xoapid_file"
    if [ -z "$XOAServer_HOME" ]; then
        export XOAServer_HOME=`cd ..; pwd`;
    fi
else
    currentPath=`cd $test1; pwd`
    # echo $currentPath
    xoapidFile="$currentPath/xoapid_file"
    
    if [ -z "$XOAServer_HOME" ];
    then
        export XOAServer_HOME=`echo $currentPath/..`;
    fi
    #echo $XOAServer_HOME
fi

if [ -e "$xoapidFile" ];
then
  echo "不要重复启动同一个xoa服务"
  exit 0;
fi

if [ -z "$JAVA_HOME" ];
then
  export JAVA_HOME=`/opt/j2sdk/`;
fi
#echo $JAVA_HOME

if [ -z "$XOAServer_MAINCLASS" ];
then
    XOAServer_MAINCLASS=com.renren.xoa2.server.Bootstrap
fi

CLASS_PATH=$XOAServer_HOME/lib/*:$XOAServer_HOME/xoaapp/WEB-INF/lib/*:$XOAServer_HOME/xoaapp/WEB-INF/classes:$CLASS_PATH

#xoa log4j configuration file, manual called by xoa framework when xoa server start; Meanwhile, user log4j configuration file(es:xoaapp/WEB-INF/classes/log4j.xml), auto called by log4j framework 
JAVA_OPTS="-Dxoa.log4j.config=$XOAServer_HOME/conf/log4j.xml"

# XCS configuration
# Skip registation to XCS step. Used only for testing phase.
if [ -z "$XCS_REGISTER_SKIP" ];
then
    XCS_REGISTER_SKIP=false
fi
JAVA_OPTS="$JAVA_OPTS -Dxcs.config=$XOAServer_HOME/conf/xcs-online.properties -Dxcs.register.skip=$XCS_REGISTER_SKIP"

# XOA param configuration
JAVA_OPTS="$JAVA_OPTS -Dxoa.base=$XOAServer_HOME"

cd $XOAServer_HOME/bin

$JAVA_HOME/bin/java $JAVA_OPTS -cp $CLASS_PATH $XOAServer_MAINCLASS $XOAServer_HOME/xoaapp/WEB-INF/xoa.xml > $XOAServer_HOME/logs/xoa2.log 2>&1 &

xoapid=`ps -ef|awk '$3=='"$$ "'{print $2}'`

echo "服务启动成功"
echo $xoapid > $xoapidFile
