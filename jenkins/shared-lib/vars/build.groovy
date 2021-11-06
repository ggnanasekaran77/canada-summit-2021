#!groovy

def call(pipelineYaml) {

    def appBuildCommand = pipelineYaml.app?.build?.command?:""
    appBuildCommand = "${appBuildCommand} -Dsonar.branch.name=${env.appBranch}"
    appBuildCommand = "${appBuildCommand}".toString()

    log.info "build appBuildCommand: ${appBuildCommand}"

    def server = Artifactory.newServer url: "https://ggnanasekaran77.jfrog.io/artifactory/", credentialsId: "artifactory"
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.useWrapper = true
    rtGradle.resolver server: server, repo: "default-maven-virtual"
    rtGradle.deployer server: server, releaseRepo: "default-maven-virtual", snapshotRepo: "default-maven-virtual"
    rtGradle.run tasks: appBuildCommand

}
