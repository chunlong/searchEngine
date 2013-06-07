#!/bin/bash

SERVER_ZIP=xoa2-server.zip
SERVER_URL=http://xoa.n.xiaonei.com/downloads/xoa-server/$SERVER_ZIP

wget $SERVER_URL
if [ $? -ne 0 ]
then
    echo "ERROR: wget $SERVER_URL error, please check the env"
    exit 255
fi

unzip $SERVER_ZIP
if [ $? -ne 0 ]
then
    echo "ERROR: unzip $SERVER_ZIP error, please check the env"
    exit 255
fi

rm $SERVER_ZIP