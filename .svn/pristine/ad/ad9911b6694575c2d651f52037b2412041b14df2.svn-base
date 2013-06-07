test1=`echo $0 | awk -F/ 'BEGIN { ORS="/"; }  \
       { i = 1; while ( i < NF ) { print $i; i++;}}'`
#echo $test1
if [ -z "$test1" ]; then
  xoapidFile="./xoapid_file"
else
  currentPath=`cd $test1; pwd`
  #echo $currentPath

  xoapidFile="$currentPath/xoapid_file"
#  echo $xoapidFile
fi

xoapid=`head -1 $xoapidFile | awk  '{print $1}'`;
#xoapid=`ps aux | grep SCREEN | awk '{print $2}'`;
echo xoa pid: $xoapid
kill -9  $xoapid;
if [ -e "$xoapidFile" ]; then
  echo "正在关闭，请稍等..."
  #Don't remove the following line. It is wating for registration remove from XCS.
  sleep 5;
  rm "$xoapidFile"
  echo "成功关闭xoa服务";
else
  echo "不存在xoa服务,关闭服务失败";
fi

