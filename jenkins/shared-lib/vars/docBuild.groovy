#!groovy

def call(imageName) {

    log.info "Docker Build started ${imageName}"
    docker.withServer('ssh://gnanam@ggnanasekaran.com') {
        docker.withRegistry('', 'docker-hub-token') {
            def dockerBuild = docker.build("${imageName}")
            dockerBuild.push('latest')
        }
    }
}