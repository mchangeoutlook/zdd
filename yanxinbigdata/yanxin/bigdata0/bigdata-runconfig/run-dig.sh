#!/bin/sh
source ../../env.sh
nohup $YANXIN_JAVA_HOME/bin/java -cp $YANXIN_CP:../../serverlibs/bigdatadig.jar:../../serverlibs/bigsort.jar com.zdd.bdc.main.Startdatadig > log.runbigdig &