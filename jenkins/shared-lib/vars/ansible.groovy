def call (playbookPath, playbookName, extraVars) {

    env.ANSIBLE_COMMAND_WARNINGS = 'false'
    env.ANSIBLE_PRIVATE_KEY_FILE = 'keys/id_rsa'
    env.PYTHONUNBUFFERED = '1'
    env.ANSIBLE_STDOUT_CALLBACK = 'debug'
    env.ANSIBLE_HOST_KEY_CHECKING = 'false'
    env.ANSIBLE_FORCE_COLOR = 'true'

    logFileFilter {
        labelledShell label: "ansible-playbook ${extraVars} ${playbookName}", script: """
        chmod +x ansible.sh
        export playbookPath="${playbookPath}"
        export playbookName="${playbookName}"
        export extraVars="${extraVars}"
        sh -x ./ansible.sh
        """
    }
}