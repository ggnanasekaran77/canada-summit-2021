#!groovy

def call(pipelineYaml) {
    log.info "defaultPipeline pipelineYaml: ${pipelineYaml}"

    def gitURL = pipelineYaml.app?.git?.url?:""
    def appBranch = "${appBranch}"?:"main"
    def playbookList = pipelineYaml.ansible?.default?:[:]

    log.info "defaultPipeline gitURL: ${gitURL}"
    log.info "defaultPipeline playbookList: ${playbookList}"
    log.info "defaultPipeline appBranch: ${appBranch}"

    pipeline {
        agent any
        options {
            timestamps()
            ansiColor('xterm')
            buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '20'))
            timeout(time: 60, unit: 'MINUTES')
            disableConcurrentBuilds()
        }
        stages {
            stage('Checkout') {
                steps {
                    git url: gitURL, branch: appBranch
                }
            }
            stage('App Build Publish') {
                steps {
                    script {
                        build(pipelineYaml)
                    }
                }
            }
            stage('Docker Build Publish') {
                when {
                    expression { return (pipelineYaml?.docker) }
                }
                steps {
                    sh "echo Docker Build Publish"
                }
            }
            stage('First Group Deploy') {
                steps {
                    script {
                        dir("ansbile") {
                            git url: "https://github.com/ggnanasekaran77/ansible.git", branch: "main"
                            playbookList.first.each {
                                def extraVars = it.extraVars
                                ansible(it.playbookPath, it.playbookName, extraVars)
                            }
                        }
                    }
                }
            }
            stage('Confirmation') {
                when {
                    expression { return (playbookList?.confirmation) }
                }
                steps {
                    script {
                        input(id: 'Proceed', message: 'Deploy to Second Group?')
                    }
                }
            }
            stage('Second group Deploy') {
                steps {
                    sh "echo Second group Deploy"
                }
            }
        }
        post {
            always {
                script {
                    publishToElastic("${pipelineYaml.pipelineName}", "cicd", "${pipelineYaml.app.projectName}", "${pipelineYaml.app.name}", "stg")
                }
            }
        }
    }
}