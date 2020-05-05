# kafka-cloud-stream-exam

## Wating until 6/14

### eccluster

1. EC2 login
2. create EC2 - three instance
3. wget https://downloads.apache.org/zookeeper/zookeeper-3.6.1/apache-zookeeper-3.6.1-bin.tar.gz
4. settings zookeepers
> apt-get install java
> > vim ~/.bash_profiles
> > JAVA_HOME=/usr/lib/jvm/java-11-amazon-corretto.x86_64/bin

> cd ~/apache-zookeeper-3.6.1-bin/conf
> vim zoo.cfg
>
> > tickTime=2000
>
> > dataDir=/var/lib/zookeeper
>
> > clientPort=2181
>
> > initLimit=20
>
> > syncLimit=5
>
> > server.1=test-broker01:2888:3888
>
> > server.2=test-broker02:2888:3888
>
> > server.3=test-broker03:2888:3888

