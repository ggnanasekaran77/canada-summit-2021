def call (playbookPath, playbookName, extraVars) {

    env.ANSIBLE_COMMAND_WARNINGS = 'false'
    env.PYTHONUNBUFFERED = '1'
    env.ANSIBLE_STDOUT_CALLBACK = 'debug'
    env.ANSIBLE_HOST_KEY_CHECKING = 'false'
    env.ANSIBLE_FORCE_COLOR = 'true'

    withCredentials([usernamePassword(credentialsId: 'ansible', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {

        logFileFilter {
            labelledShell label: "ansible-playbook ${extraVars} ${playbookName}", script: """
                chmod +x ansible.sh
                export playbookPath="${playbookPath}"
                export playbookName="${playbookName}"
                export extraVars="${extraVars} -e ansible_ssh_user=${USERNAME} -e ansible_become_pass=${PASSWORD}"
                sh -x ./ansible.sh
            """
        }

    }
}