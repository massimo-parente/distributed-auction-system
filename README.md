A distributed fantasy football auction system

Play2, Angular2, Typescript, Web-sockets, Slick

##Installation

Clone repository

Install activator:

https://www.lightbend.com/activator/download

##Usage

###Run from command line

cd into root directory and run:

activator clean stage

###Build executable artifact

cd into root directory and run:

activator clean dist

Artifacts will be located in target/universal

For all supported formats refer to 

https://www.playframework.com/documentation/2.5.x/Deploying 

##Configuration

The application currently run on H2 database.

To replace H2 with a different databse edit conf/application.conf file and replace the following lines with the required driver details
```
slick.dbs.default.driver="slick.driver.H2Driver$"
slick.dbs.default.db.driver="org.h2.Driver"
slick.dbs.default.db.url="jdbc:h2:mem:play;MODE=MYSQL"
```

See https://www.playframework.com/documentation/2.5.x/PlaySlick#database-configuration


