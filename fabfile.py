#!/usr/bin/env python

from __future__ import with_statement
from fabric.api import local, put, run, env

env.use_ssh_config = True


def build():
    local('sbt clean compile assembly')


def deploy():
    cuttrdir = '/home/%s/cuttr' % env.GLITCHUSER
    put('assembled/cuttr.jar', cuttrdir)
    run('chmod +x %s/cuttr.jar' % cuttrdir)
    run('chown %s:%s %s/cuttr.jar' % (env.GLITCHUSER, env.GLITCHGROUP, cuttrdir))
