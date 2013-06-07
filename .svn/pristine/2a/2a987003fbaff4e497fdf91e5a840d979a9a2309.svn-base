#!/bin/bash

# To get the base idl

IDL_BASE=http://xoa.n.xiaonei.com/downloads/thrift/thrift-idl.tar.gz
IDL_LOCAL_DIR=src/main/thrift

LOCAL_THRIFT=`ls $IDL_LOCAL_DIR/*.thrift`
echo $LOCAL_THRIFT

if [ -e $IDL_LOCAL_DIR/*.thrift ]
then
    echo "INFO: Get base idl for $LOCAL_THRIFT"
else
    echo "ERROR: please generate you *.thrift at \"$IDL_LOCAL_DIR\" first!"
    exit 255
fi

BASE_IDL_NEEDED=`grep "^include" $LOCAL_THRIFT | awk '{print $2}' | sed s/\\"//g`
echo $BASE_IDL_NEEDED

wget $IDL_BASE 
if [ $? -ne 0 ]
then
    echo "ERROR: wget $IDL_BASE error, please check the env"
    exit 255
fi

mkdir -p $IDL_LOCAL_DIR/tmp
mv thrift-idl.tar.gz $IDL_LOCAL_DIR/tmp
cd $IDL_LOCAL_DIR/tmp
tar xzvf thrift-idl.tar.gz
if [ $? -ne 0 ]
then
    echo "ERROR: tar at $IDL_LOCAL_DIR error, please check the env"
    exit 255
fi

for i in $BASE_IDL_NEEDED
do
    dir=`dirname $i`
    mkdir -p ../${dir}
    cp $i ../${dir}
    if [ $? -ne 0 ]
    then
        echo "ERROR: $i not exist!"
        exit 255
    fi
done

cd ..
rm -rf ./tmp