#!/bin/bash

while :
do
    tail -f  /root/bslog  | nc -lk -s basestation -p 12345
    if [ $? -ne 0 ] ; then
	exit 1
    fi
done
