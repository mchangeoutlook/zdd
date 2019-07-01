#!/bin/sh
source ../../env.sh
nohup $YANXIN_JAVA_HOME/bin/java -cp $YANXIN_CP com.zdd.bdc.client.main.Startuniqueindexscale yanxin pagedkey > log.runscale &
