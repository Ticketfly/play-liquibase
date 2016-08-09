[![Codacy Badge](https://api.codacy.com/project/badge/grade/7577ae541fbe4adc8d07f76f2c88ae06)](https://www.codacy.com/app/dragisak/play-liquibase) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ticketfly/play-liquibase_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ticketfly/play-liquibase_2.11) [![Dependencies](https://app.updateimpact.com/badge/692410697593786368/play-liquibase.svg?config=compile)](https://app.updateimpact.com/latest/692410697593786368/play-liquibase)

# Play 2.4+ Liquibase Migration Module

Runs [Liquibase](http://www.liquibase.org) migrations on Play application startup.

* [Play 2.4  Liquibase Migration Module](#play-24-liquibase-migration-module)
  * [Adding Liquibase Module to your Play Scala project](#adding-liquibase-module-to-your-play-scala-project)
  * [Configuration](#configuration)
  * [Using Liquibase](#using-liquibase)
    * [Loading changelog files from different location](#loading-changelog-files-from-different-location)
      * [Loading external changelog files](#loading-external-changelog-files)
    * [Using include and <code>includeAll</code> tags](#using-include-and-includeall-tags)
    * [Using contexts](#using-contexts)
    * [Disabling Liquibase migrations](#disabling-liquibase-migrations)
    * [Testing With In-memory Database](#testing-with-in-memory-database)
  * [Copyright and License](#copyright-and-license)

## Adding Liquibase Module to your Play Scala project

Add dependency to your `build.sbt`:

```scala
libraryDependencies += "com.ticketfly" %% "play-liquibase" % "1.3-SNAPSHOT"
```

No additional code changes are necessary. It uses [Play Dependency Injection](https://www.playframework.com/documentation/latest/ScalaDependencyInjection)
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

Liquibase Module uses Liquibase 3.5.1

Example changelog.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.5.xsd">

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

Place your `changelog.xml` file in the `conf/liquibase` directory. That will make it a part of Play distribution.

You can override name and path to changelog file by setting `liquibase.changelog` configuration parameter. Default is `liquibase/changelog.xml`

For details on using Liquibase, go to: [www.liquibase.org](http://www.liquibase.org)

### Loading changelog files from different location

By default, play-liquibase module will look for `liquibase/changelog.xml` in the classpath.
You can override that by specifying a different loader or different location of changelog location.

#### Loading external changelog files

```
liquibase.loader = FILE 
liquibase.changelog = /path/to/changelog.xml
```

Default values are:
```
liquibase.loader = CLASSPATH 
liquibase.changelog = "liquibase/changelog.xml"
```


### Using `include` and `includeAll` tags

Example changelog.xml (if you place your schema changelogs in `conf/liquibase/schema` directory and trigger in `conf/liquibase/triggers` directory):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.5.xsd">

    <includeAll path="./schema" relativeToChangelogFile="true"/>
    <include path="./triggers/trigger-1.xml" relativeToChangelogFile="true"/>

</databaseChangeLog>
```

### Using contexts

Liquibase contexts can be used to maintain a different set of change sets for different environments or uses.  For example, you may have one set to maintain the production schema
and one set to maintain the test schema, along with a small set of test data

Context is an attribute of the change set
```xml
<changeSet id="2" author="bob" context="test">
        ...
</changeSet>
```

To run the "test" context only, add to your liquibase configuration in application.conf
```
liquibase.contexts = ["test"]
```

### Disabling Liquibase migrations

To disable running Liquibase on startup, you can set
```
liquibase.enable = false
```

You can disable Liquibase from command line with `-Dliquibase.enable=false`.

For details on configuring Play app, see [Play Production Configuration](https://www.playframework.com/documentation/2.4.x/ProductionConfiguration)

### Testing With In-memory Database

There is a special options in H2 url to tell H2 to keep schema after Liquibase has finished: `DB_CLOSE_DELAY=-1`

Also, you have to make sure that it does not force table name to uppercase with `DATABASE_TO_UPPER=false`

Example:

```scala
  val appWithMemoryDatabase = FakeApplication(
    additionalConfiguration = {
      val driver = "org.h2.Driver"
      val url = s"jdbc:h2:mem:test${Random.nextInt()}:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"
      Map(
        "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
        "slick.dbs.default.db.driver" -> driver,
        "slick.dbs.default.db.url" -> url,
        "slick.dbs.default.db.user" -> "sa",
        "slick.dbs.default.db.password" -> "",
        "liquibase.driver" -> driver,
        "liquibase.url" -> url,
        "liquibase.user" -> "sa",
        "liquibase.password" -> ""
      )
    }
  )

  "save and query" in new WithApplication(appWithMemoryDatabase) {
        ...
        val postRequest = FakeRequest(POST, "/test").withJsonBody(Json.toJson(payload))
        val Some(saveResult) = route(postRequest)
        status(saveResult) must equalTo(OK)
        ...
  }

```


## Copyright and License

All code is available to you under the MIT license, available at [http://opensource.org/licenses/MIT](http://opensource.org/licenses/MIT) and also
in the LICENSE file.

Copyright Ticketfly, Inc., 2016.
