package com.ticketfly.play.liquibase

import java.io.StringWriter
import javax.inject.Singleton

import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.{ClassLoaderResourceAccessor, FileSystemResourceAccessor}
import liquibase.{Contexts, LabelExpression, Liquibase}
import play.api._
import play.api.inject.{Binding, Module}

import scala.collection.JavaConverters._

/**
 * Play Liquibase module. Automatically runs Liquibase migrations on Play app startup.
 *
 * To add to your Scala Play project:
 *
 * ```
 * libraryDependencies += "com.ticketfly" %% "play-liquibase" % currentVersion
 * ```
 *
 * See: [[https://www.playframework.com/documentation/latest/ScalaDependencyInjection Play Scala Dependency Injection]]
 *
 */
class PlayLiquibaseModule extends Module {

  override def bindings (environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    // eagerly bind module so it runs on Play startup
    bind[PlayLiquibase].to(new PlayLiquibase(environment, configuration)).eagerly()
  )

}

@Singleton
class PlayLiquibase(environment: Environment, config: Configuration) {

  private final val log = Logger(classOf[PlayLiquibase])

  private val contexts = config.getStringList("liquibase.contexts").map(l => new Contexts(l)).getOrElse(new Contexts())

  // Constructor
  upgradeSchema(config.getString("app.version"))

  /**
   * Run Liquibase schema upgrade
   *
   * @param tag Optionally tag schema version
   */
  def upgradeSchema (tag: Option[String] = None): Unit = {
    liquibase() match {
      case Some(lb) =>
        log.info("Running liquibase migrations")
        lb.update(contexts)
        tag.foreach(t => lb.tag(t))
      case None =>
    }
  }


  /**
   * Show pending schema changes in the log but don't run them.
   */
  def showSql (): Unit = liquibase().foreach {
    lb =>
      val writer = new StringWriter()
      lb.update(contexts, writer)
      log.info(writer.toString)
  }


  /**
   * Check if there are pending schema changes.
   *
   * @return true if there are pending changes
   */
  def needsUpgrade: Boolean = liquibase().exists { lb =>
    val unrunChanges = lb.listUnrunChangeSets(contexts, new LabelExpression()).asScala
    unrunChanges.foreach {
      change => log.warn(s"Unrun changeset: ${change.getId}, ${change.getAuthor}, ${change.getDescription}, ${change.getComments}")
    }
    unrunChanges.nonEmpty
  }


  /**
   * Force unlock Liquibase tables
   */
  def unlock (): Unit = liquibase().foreach {
    lb =>
      log.info("Releasing liquibase locks")
      lb.forceReleaseLocks()
  }


  protected def liquibase(): Option[Liquibase] = {

    val liquibaseConfOpt = config.getConfig("liquibase")

    val enableLiquibase = liquibaseConfOpt.flatMap(_.getBoolean("enable")).getOrElse(true)

    val urlOpt          = liquibaseConfOpt.flatMap(_.getString("url"))
    val driverOpt       = liquibaseConfOpt.flatMap(_.getString("driver"))
    val usernameOpt     = liquibaseConfOpt.flatMap(_.getString("user"))
    val passwordOpt     = liquibaseConfOpt.flatMap(_.getString("password"))
    val changelogOpt    = liquibaseConfOpt.flatMap(_.getString("changelog"))

    if (enableLiquibase) {
      val liquibaseOpt = for {
        url         <- urlOpt
        username    <- usernameOpt
        password    <- passwordOpt
        driver      <- driverOpt
        changelogTemp   <- changelogOpt
        database          = CommandLineUtils.createDatabaseObject(
          new ClassLoaderResourceAccessor(environment.classLoader),
          url,
          username,
          password,
          driver,
          null,   // defaultCatalogName
          null,   // defaultSchemaName
          false,  // outputDefaultCatalog,
          false,  // outputDefaultSchema,
          null,   // databaseClass,
          null,   // driverPropertiesFile,
          null,   // propertyProviderClass,
          null,   // liquibaseCatalogName,
          null,   // liquibaseSchemaName,
          null,   // databaseChangeLogTableName
          null    // databaseChangeLogLockTableName
        )

        resourceAccessor  = if(changelogTemp.startsWith("classpath:")) {
          new ClassLoaderResourceAccessor(environment.classLoader)
        } else {
          new FileSystemResourceAccessor(environment.rootPath.getPath)
        }
        changelog = changelogTemp.replaceFirst("classpath:", "")
      } yield new Liquibase(changelog, resourceAccessor, database)

      if(liquibaseOpt.isEmpty) {
        log.warn("Liquibase is not configured")
      }

      liquibaseOpt
    } else {
      log.info("Liquibase is disabled. No migrations will be run")
      None
    }
  }

}
