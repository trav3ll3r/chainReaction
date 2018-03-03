#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

if [ "$1" == "-c" ]
then
  adb logcat -c
fi

clear;

adb logcat | egrep -i "VisualiseChainActivity"
