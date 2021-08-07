#!groovy
package backup

import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule

def username = System.getenv("username")?:"admin"
def password = System.getenv("password")?:"admin"

def instance = Jenkins.getInstance()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(username, password)

Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
