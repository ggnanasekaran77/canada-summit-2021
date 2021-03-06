#!/usr/bin/env groovy

def call() {

    def pipelineMap = new URL ("http://configserver:8888/pipelinemap/default/main/${JOB_NAME}.yml").getText()
    def pipelineYaml = readYaml text: pipelineMap
    pipelineYaml['pipelineName'] = pipelineYaml?.pipelineName?:"defaultPipeline"

    log.info "cicd JOB_NAME ${JOB_NAME}"
    log.info "cicd pipelineName ${pipelineYaml.pipelineName}"
    "${pipelineYaml.pipelineName}"(pipelineYaml)


}
