#!groovy

def call() {

    docker.withRegistry('', 'docker') {
        def dockerBuild = docker.build("ggnanasekaran77/${env.appName}")
        dockerBuild.push()
        dockerBuild.push('latest')
    }
}