# -*- coding: utf-8 -*-
import os
import sys

action = sys.argv[1]

def renameFile(dir):
    print "参数是" + action

    if os.path.exists(dir) :
        print "dir exists"
    else :
        print "no exists"
    list = os.listdir(dir) #列出文件夹下所有的目录与文件
    for i in range(0,len(list)):
        path = os.path.join(dir,list[i])
        if os.path.isdir(path):
            renameFile(path)
        if os.path.isfile(path):
            print path#你想对文件的操作
            if action == 'sh':
                addSh(path)
            elif action == 'unsh':
                delSh(path)
            else :
                print "参数错误，想要加上后缀名参数为sh，想要去掉后缀名参数为unsh"


def addSh(filename):
    os.rename(filename,filename + '.sh')

def delSh(filename):
    finames = os.path.splitext(filename)
    os.rename(filename,finames[0])



if __name__=='__main__':

    dir = './build'
    renameFile(dir)
    dir = './config'
    renameFile(dir)
    dir = './src'
    renameFile(dir)
    dir = './static'
    renameFile(dir)
