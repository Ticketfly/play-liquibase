# Play 2.4 Liquibase Migration Module

Runs [Liquibase](http://www.liquibase.org) migrations on Play application startup.


## Adding Liquibase Module to your Play project

Add dependency to your `build.sbt`:

```scala
libraryDependencies += "com.ticketfly" %% "play-liquibase" % "0.1"
```

No additional code changes are necessary. It uses [Play 2.4 Dependency Injection](https://www.playframework.com/documentation/latest/ScalaDependencyInjection)
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

Liquibase Module uses Liquibase 3.3.x.

Example changelog.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd">


    <changeSet id="1" author="dragisak">
        <comment>Create a table</comment>
        <createTable tableName="my_table">
            <column name="id" type="bigint" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="event_id" type="bigint">
                <constraints nullable="false"/>
            </column>
            <column name="ticket_type" type="varchar(30)">
                <constraints nullable="false"/>
            </column>
            <column name="onsale_date" type="datetime">
                <constraints nullable="false"/>
            </column>
            <column name="offsale_date" type="datetime">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>

</databaseChangeLog>
```

Place your `changelog.xml` file in your app's `config` directory. That will make it a part of Play distribution and accessible from classpath.

You can override name and path to changelog file by setting `liquibase.changelog` configuration parameter. Default is `changelog.xml`

For details on using Liquibase, go to: [www.liquibase.org](http://www.liquibase.org)
