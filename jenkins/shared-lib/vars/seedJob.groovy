#!groovy

def call() {
    def jobPattern = "${params.pattern}".trim()
    node {
        stage ("Seed Job Checkout") {
            git url: 'https://github.com/ggnanasekaran77/pipelinemap.git', branch: 'main'
        }
        stage ("pipelinemap jobs") {
            jobList = sh (script: """ git ls-tree --full-tree -r --name-only HEAD|egrep -iv ".gitignore|default|readme.md|.groovy" | sed "s/.yml//" | egrep "${jobPattern}" || echo "devops/configserver" """, returnStdout: 'True').trim().split('\n')
            log.info "Pipelinemap jobList ${jobList}"
            jobList.each {
                def folderName = it.split('/')[0]
                def jobName = it.split('/')[1]
                log.info "Pipelinemap folderName ${folderName}"
                log.info "Pipelinemap jobName ${jobName}"
                jobDsl scriptText: """
                    folder ("${folderName}") {
                        displayName("${folderName}")
                        description("${folderName}")
                    }
                """
                jobDsl scriptText: """
                    pipelineJob ("${folderName}/${jobName}") {
                        parameters {
                            stringParam('appBranch', 'develop', 'Please provide Application Branch Name')
                        }
                        definition {
                            cps {
                                sandbox(true)
                                script("cicd()")
                            }
                        }
                    }
                """
            }
        }
        stage ("Groovy Script jobs") {
            jobList = sh (script: """ find . -name '*.groovy' | egrep "${jobPattern}" || echo "configserver.groovy" """, returnStdout: 'True').trim().split('\n')
            log.info "GroovyScript jobList ${jobList}"
            jobList.each {
                jobDsl targets: "${it}"
            }
        }
        stage ("publish result to elastic") {
            publishToElastic("seedPipeline", "seed", "devops", "seed", "stg")
        }
    }
}