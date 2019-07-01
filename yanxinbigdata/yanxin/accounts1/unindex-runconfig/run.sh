#!/bin/sh
source ../../env.sh
nohup $YANXIN_JAVA_HOME/bin/java -cp $YANXIN_CP com.zdd.bdc.server.main.Startuniqueindexserver > log.runbigunindexserver &
