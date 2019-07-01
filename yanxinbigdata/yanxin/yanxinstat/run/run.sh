#!/bin/sh
source ../../env.sh
nohup $YANXIN_JAVA_HOME/bin/java -cp $YANXIN_CP:yanxinstat.jar:yanxin.jar com.xinzyan.yanxin.stat.main.Start > log.runyanxinstat &