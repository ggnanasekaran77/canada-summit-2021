#!/usr/bin/env groovy

def call() {

    /* env.projectName = "${JOB_NAME}".tokenize('/')[0]
    env.appName = "${JOB_NAME}".tokenize('/')[-3]
    env.appBranch = extraVarsParser("appBranch")?:"${JOB_BASE_NAME}"
    def pipelineMapVars = getPipelineMapVars()
    def pipelineName = pipelineMapVars?.pipelineName?:"canaryPipeline"
    def pipelineVars = pipelineVars()
    pipelineMapVars << pipelineVars
    setenv(pipelineMapVars)
    log.info "cicd pipelineName: ${pipelineName}"
    "${pipelineName}"(pipelineMapVars) */

    def pipelineMap = new URL ("http://configserver:8888/pipelinemap/default/main/${JOB_NAME}.yml").getText()
    def pipelineYaml = readYaml text: pipelineMap
    pipelineYaml['pipelineName'] = pipelineYaml?.pipelineName?:"defaultPipeline"

    log.info "cicd JOB_NAME ${JOB_NAME}"
    log.info "cicd pipelineMap ${pipelineYaml}"
    log.info "cicd pipelineName ${pipelineYaml.pipelineName}"
    "${pipelineYaml.pipelineName}"(pipelineYaml)


}
