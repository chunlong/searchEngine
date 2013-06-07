#!/bin/sh

#This script is used for lauch XoaAdmin Console.
#@author wenxin.xue@renren-inc.com
#@version 1.000

#check JAVA_HOME environment variable
if [ -z "$JAVA_HOME" ];
then
  export JAVA_HOME=`/opt/j2sdk/`;
fi


if [ -z "$XoaAdmin_HOME" ];
then
  XoaAdmin_HOME=`cd ..; pwd`;
fi

if [ -z "$XoaAdmin_MAINCLASS" ];
then
  XoaAdmin_MAINCLASS=com.renren.xcs.optool.XoaAdminMain
fi

CLASS_PATH=$XoaAdmin_HOME/bin/xcs-optool.jar:$CLASS_PATH

echo $CLASS_PATH

echo $XoaAdmin_MAINCLASS

#cd $XoaAdmin_HOME/bin

$JAVA_HOME/bin/java -cp $CLASS_PATH $XoaAdmin_MAINCLASS $@


