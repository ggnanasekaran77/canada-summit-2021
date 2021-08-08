#!groovy

def call(imageName) {

    docker.withServer('ssh://gnanam@ggnanasekaran.com') {
        docker.withRegistry('', 'docker') {
            def dockerBuild = docker.build("${imageName}")
            dockerBuild.push()
            dockerBuild.push('latest')
        }
    }
}