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
    
    config.vm.box = "geerlingguy/centos7"
    
    config.ssh.forward_x11 = true

    config.vm.provision "bootstrap", type: "shell", inline: <<-SHELL
        #echo "Updating base"
        #sudo yum -y update
        
        echo "Installing Extra Package for Enterprise Linux (EPEL)"
        sudo yum -y install epel-release
        
        echo "Installing system utilities"
        sudo yum -y install java-1.8.0-openjdk-headless.x86_64
        #sudo yum -y install pciutils traceroute
        #sudo yum -y install policycoreutils policycoreutils-python
        #sudo yum -y install wget unzip
        #sudo yum -y install mlocate unzip
        
        #echo "Finalizing updates"
        #sudo yum -y update
    SHELL

    Dir.glob('servers/*.json') do |file|
        json = (JSON.parse(File.read(file)))['server']
        id = json['id']
        hostname = json['hostname']
        memory = json['memory']
        cpus = json['cpus']
        network = json['network']
        rabbit = json['rabbitmq']

        config.vm.define id do |server|
            server.vm.hostname = hostname
            server.vm.define id

            server.vm.provider "virtualbox" do |vb|
                vb.gui = false
                vb.name = id
                vb.cpus = cpus
                # Customize the amount of memory
                vb.memory = memory
            end

            # contains a list of possible bridge adapters and the first one to successfully
            # bridged will be used
            server.vm.network network['type'], ip: network['ip'], bridge: network['bridge']

            network['ports'].each do |p|
                server.vm.network "forwarded_port", guest: p['guest'], host: p['host']
            end
            
            network['hosts'].each do |h|
                hostname = h["hostname"]
                ip = h["ip"]
                server.vm.provision "hosts", type: "shell", args: [ hostname, ip ], inline: <<-SHELL
                    echo "$2 $1 $1" >> /etc/hosts
                SHELL
            end
            
            config.vm.provision "rabbitmq", type: "shell", args: [rabbit["vhost"], rabbit["cluster"]], inline: <<-SHELL
                echo "Installing RabbitMQ"
                sudo yum -y install rabbitmq-server
            
                echo "Enabling RabbitMQ Management Plugin"
                rabbitmq-plugins enable rabbitmq_management
                
                # firewall is off for Vagrant
                #echo "Configuring RabbitMQ firewalls"
                #sudo firewall-cmd --zone=public --add-port=5672/tcp --permanent
                #sudo firewall-cmd --zone=public --add-port=15672/tcp --permanent
                #sudo firewall-cmd --reload

                echo "Restarting RabbitMQ"
                sudo systemctl restart rabbitmq-server
                sudo systemctl stop rabbitmq-server

                echo "Configuring /var/lib/rabbitmq/.erlang.cookie"
                rm -f /var/lib/rabbitmq/.erlang.cookie
                echo "AQKZMIKKNOZHFEENNNQF" > /var/lib/rabbitmq/.erlang.cookie
                chown rabbitmq:rabbitmq /var/lib/rabbitmq/.erlang.cookie
                chmod 400 /var/lib/rabbitmq/.erlang.cookie

                echo "Starting up rabbitmq-server"
                sudo systemctl restart rabbitmq-server

                rabbitmqctl stop_app
                rabbitmqctl reset
                rabbitmqctl start_app

                if [ ! -z "$2" ]; then
                    echo "Setting up cluster to $2"
                    rabbitmqctl stop_app
                    rabbitmqctl reset
                    rabbitmqctl join_cluster $2
                    rabbitmqctl start_app
                    rabbitmqctl cluster_status
                else
                    echo "Setting up virtual host: $1"
                    rabbitmqctl add_vhost $1
                    rabbitmqctl set_permissions -p $1 guest ".*" ".*" ".*"
                fi
            SHELL
        end
    end

end
