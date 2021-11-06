#!groovy

def call(def pipelineName, def jobType, def projectName="unknown", def appName="unknown", def targetEnv="unknown") {

    def dateTime = new Date().format("yyyy-MM-dd HH:mm:ss")
    def Id = new Date().format("yyyyMMddHHmmssms")
    def elasticURL = "http://elasticsearch:9200"
    def indexName = "cicd-jenkins-2021"
    def nodeName = "${NODE_NAME}"
    def json = new groovy.json.JsonBuilder()
    def status = "${currentBuild.result}"

    if(jobType == "seed") {
        status = "SUCCESS"
    }

    def jobInfo = """ { 
                        "job_base_name": "${JOB_BASE_NAME}",  
                        "job_name": "${JOB_NAME}",
                        "job_type": "${jobType}",
                        "pipeline": "${pipelineName}",
                        "date": "${dateTime}",
                        "status": "${status}",
                        "build_number": "${currentBuild.number}",
                        "projectName": "${projectName}",
                        "appName": "${appName}",
                        "targetEnv": "${targetEnv}"
                    }"""
    log.info "publishResultToES ${jobInfo}"

    def elasticConnection = new URL("${elasticURL}").openConnection();
    def elasticConnectionRC = elasticConnection.getResponseCode();
    if(elasticConnectionRC.equals(200)) {
        def elasticConnectionPost = new URL("${elasticURL}/${indexName}/_doc/${Id}").openConnection();
        elasticConnectionPost.setRequestMethod("POST")
        elasticConnectionPost.setDoOutput(true)
        elasticConnectionPost.setRequestProperty("Content-Type", "application/json")
        elasticConnectionPost.getOutputStream().write(jobInfo.getBytes("UTF-8"));
        def elasticConnectionPostRC = elasticConnectionPost.getResponseCode();
        println(elasticConnectionPostRC);
        if(elasticConnectionPostRC.equals(201)) {
            log.info "publishResultToES elasticConnectionPostRC: ${elasticConnectionPostRC}"
        } else {
            log.info "publishResultToES Unable to publish elasticConnectionPostRC: ${elasticConnectionPostRC}"
        }
    } else {
        log.info "publishResultToES Unable to reach/open connection with ${elasticURL} elasticConnectionRC: ${elasticConnectionRC}"
    }
}
