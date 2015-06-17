# Play 2.4 Liquibase Migration

Runs [Liquibase](http://www.liquibase.org) migrations on Play app startup.


## Adding to your Play project

Add dependency to your `build.sbt`:

```scala
resolvers += "TFly Release" at "http://build.ticketfly.com/artifactory/libs-release"

credentials += Credentials(Path.userHome / ".artifactory" / ".credentials")

libraryDependencies += "com.ticketfly" %% "play-liquibase" % "0.1"
```

No additional code changes are necessary. It used [Play 2.4 Dependency Injection](https://www.playframework.com/documentation/latest/ScalaDependencyInjection)
to eagerly run migrations on startup.

## Configuration

Add to `application.conf`:

```
liquibase {
    url      = "jdbc:mysql://localhost/myschema?logger=com.mysql.jdbc.log.Slf4JLogger"
    driver   = "com.mysql.jdbc.Driver"
    user     = "ticketfly"
    password = "bar123"
}

```

If you are using Slick with Play, you can reference Slick config:

```
liquibase = ${slick.dbs.default.db}
```


## Using Liquibase

Place your `changelog.xml` file in `config` directory. That will make it a part of Play distribution and accessible from classpath.

You can override name and path to changelog file by setting `liquibase.changelog` configuration parameter. Default is `changelog.xml`

For details on using Liquibase, go to: [www.liquibase.org](http://www.liquibase.org)
