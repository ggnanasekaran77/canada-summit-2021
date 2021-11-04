#!groovy

def call(imageName) {

    log.info "docBuild imageName: ${imageName}"
    docker.withServer('ssh://gnanam@ggnanasekaran.com') {
        docker.withRegistry('', 'docker') {
            def dockerBuild = docker.build("${imageName}")
            dockerBuild.push('latest')
        }
    }
}