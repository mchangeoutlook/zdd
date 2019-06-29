#!/bin/sh
source ../../env.sh
nohup $JAVA_HOME/bin/java -cp $YANXIN_CP com.zdd.bdc.server.main.Startconfigserver > log.runbigconfigserver &