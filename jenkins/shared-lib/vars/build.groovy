#!groovy

def call(pipelineYaml) {

    env.SONAR_TOKEN = 'db50ee5b18eda7cf1fdd59a3a7beb87ee4ba2c0d'
    def appBuildCommand = pipelineYaml.app?.build?.command?:""
    def appBranch = "${appBranch}"?:"main"
    appBuildCommand = "${appBuildCommand} -Dsonar.branch.name=${appBranch}".toString()

    log.info "build appBuildCommand: ${appBuildCommand}"

    def server = Artifactory.newServer url: "https://ggnanasekaran77.jfrog.io/artifactory/", credentialsId: "artifactory"
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.useWrapper = true
    rtGradle.resolver server: server, repo: "default-maven-virtual"
    rtGradle.deployer server: server, releaseRepo: "default-maven-virtual", snapshotRepo: "default-maven-virtual"
    rtGradle.run tasks: appBuildCommand
}
