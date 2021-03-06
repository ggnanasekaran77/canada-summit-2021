#!groovy

def call(pipelineYaml) {
    log.info "defaultPipeline pipelineYaml: ${pipelineYaml}"

    def gitURL = pipelineYaml.app?.git?.url?:""
    def appBranch = "${appBranch}"?:""
    env.appName = pipelineYaml.app.name
    def playbookList
    if (appBranch == "main") {
        playbookList = pipelineYaml.ansible?.prd?:[:]
        env.targetEnv = "prd"
    } else {
        playbookList = pipelineYaml.ansible?.default?:[:]
        env.targetEnv = "stg"
    }

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
        parameters {
            string(name: 'appBranch', defaultValue: 'main', description: "Application Branch name")
        }
        stages {
            stage('Checkout') {
                when {
                    expression { return (params.appBranch) }
                }
                steps {
                    git url: gitURL, branch: appBranch
                }
            }
            stage('App Build Publish') {
                when {
                    expression { return (params.appBranch) }
                }
                steps {
                    script {
                        build(pipelineYaml)
                    }
                }
            }
            stage('Docker Build Publish') {
                when {
                    expression { return (pipelineYaml?.docker && params.appBranch) }
                }
                steps {
                    script {
                        docBuild(pipelineYaml.docker.imageName)
                    }
                }
            }
            stage('First Group Deploy') {
                when {
                    expression { return (playbookList?.first) }
                }
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
                when {
                    expression { return (playbookList?.second) }
                }
                steps {
                    script {
                        dir("ansbile") {
                            playbookList.second.each {
                                def extraVars = it.extraVars
                                ansible(it.playbookPath, it.playbookName, extraVars)
                            }
                        }
                    }
                }
            }
        }
        post {
            always {
                script {
                    publishToElastic("${pipelineYaml.pipelineName}", "cicd", "${pipelineYaml.app.projectName}", "${pipelineYaml.app.name}", "${env.targetEnv}")
                }
                cleanWs()
                dir("${env.WORKSPACE}@tmp") {
                    deleteDir()
                }
                dir("${env.WORKSPACE}@script") {
                    deleteDir()
                }
                dir("${env.WORKSPACE}@script@tmp") {
                    deleteDir()
                }
            }
        }
    }
}