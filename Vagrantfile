# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
    # The most common configuration options are documented and commented below.
    # For a complete reference, please see the online documentation at
    # https://docs.vagrantup.com.
    
    config.ssh.forward_x11 = true

    # Every Vagrant development environment requires a box. You can search for
    # boxes at https://vagrantcloud.com/search.
    config.vm.box = "geerlingguy/centos7"
    config.vm.define "qrmi"
    config.vm.hostname = "qrmi"

    # Disable automatic box update checking. If you disable this, then
    # boxes will only be checked for updates when the user runs
    # `vagrant box outdated`. This is not recommended.
    # config.vm.box_check_update = false

    # Create a forwarded port mapping which allows access to a specific port
    # within the machine from a port on the host machine. In the example below,
    # accessing "localhost:8080" will access port 80 on the guest machine.
    # NOTE: This will enable public access to the opened port
    config.vm.network "forwarded_port", guest: 5672, host: 6859
    config.vm.network "forwarded_port", guest: 15672, host: 6858

    # Create a forwarded port mapping which allows access to a specific port
    # within the machine from a port on the host machine and only allow access
    # via 127.0.0.1 to disable public access
    # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"

    # Create a private network, which allows host-only access to the machine
    # using a specific IP.
    config.vm.network "private_network", ip: "10.10.1.2"

    # Create a public network, which generally matched to bridged network.
    # Bridged networks make the machine appear as another physical device on
    # your network.
    # config.vm.network "public_network"

    # Share an additional folder to the guest VM. The first argument is
    # the path on the host to the actual folder. The second argument is
    # the path on the guest to mount the folder. And the optional third
    # argument is a set of non-required options.
    # config.vm.synced_folder "../data", "/vagrant_data"

    # Provider-specific configuration so you can fine-tune various
    # backing providers for Vagrant. These expose provider-specific options.
    # Example for VirtualBox:
    #
    config.vm.provider "virtualbox" do |vb|
        # Display the VirtualBox GUI when booting the machine
        # vb.gui = true
        vb.name = "qrmi"
    
        # Customize the amount of memory on the VM:
        vb.memory = "1024"
    end
    
    # View the documentation for the provider you are using for more
    # information on available options.

    # Enable provisioning with a shell script. Additional provisioners such as
    # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
    # documentation for more information about their specific syntax and use.
    
    config.vm.provision "bootstrap", type: "shell", inline: <<-SHELL
        echo "Updating base"
        sudo yum -y update
        
        echo "Installing Extra Package for Enterprise Linux (EPEL)"
        sudo yum -y install epel-release
        
        echo "Installing system utilities"
        sudo yum -y install pciutils
        sudo yum -y install policycoreutils policycoreutils-python
        sudo yum -y install wget unzip
        sudo yum -y install mlocate unzip
        
        echo "Finalizing updates"
        sudo yum -y update
    SHELL
    
    config.vm.provision "rabbitmq", type: "shell", inline: <<-SHELL
        echo "Installing RabbitMQ"
        sudo yum -y install rabbitmq-server
        
        echo "Enabling RabbitMQ Management Plugin"
        rabbitmq-plugins enable rabbitmq_management
        
        echo "Starting up rabbitmq-server"
        sudo systemctl start rabbitmq-server
        
        echo "Setting up virtual host: qrmi"
        rabbitmqctl add_vhost qrmi
        rabbitmqctl set_permissions -p qrmi guest ".*" ".*" ".*"
    SHELL
end
