def call(Map config = [:]) {
    // Extract parameters
    def playbook = config.playbook ?: error('Playbook path is required')
    def inventory = config.inventory ?: 'inventory.yml'
    def extraArgs = config.extraArgs ?: ''

    // Execute Ansible playbook
    sh "ansible-playbook -i ${inventory} ${extraArgs} ${playbook}"
}
