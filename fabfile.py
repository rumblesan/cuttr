#!/usr/bin/env python

from __future__ import with_statement
from fabric.api import local, put, run


def build():
    local('sbt clean compile assembly')


def deploy(user, group):
    put('assembled/cuttr.jar', '~/tumblrscripts/cuttr')
    run('chmod +x /home/%s/tumblrscripts/cuttr/cuttr.jar' % user)
    run('chown %s:%s /home/%s/tumblrscripts/cuttr/cuttr.jar' %
        (user, group, user))
