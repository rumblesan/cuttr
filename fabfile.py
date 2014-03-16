#!/usr/bin/env python

from __future__ import with_statement
from fabric.api import local, put, run, env

env.use_ssh_config = True


def build():
    local('sbt clean compile assembly')


def deploy():
    put('assembled/cuttr.jar', '~/tumblrscripts/cuttr')
    run('chmod +x /home/%s/tumblrscripts/cuttr/cuttr.jar' % env.GLITCHUSER)
    run('chown %s:%s /home/%s/tumblrscripts/cuttr/cuttr.jar' %
        (env.GLITCHUSER, env.GLITCHGROUP, env.GLITCHUSER))
