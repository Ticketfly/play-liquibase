package com.ticketfly.play.liquibase

import java.io.StringWriter
import javax.inject.Singleton

import liquibase.{LabelExpression, Contexts, Liquibase}
import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.ClassLoaderResourceAccessor
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

  // Constructor
  upgradeSchema(Some(config.getString("app.version").getOrElse("0.0")))

  private def liquibase() = {

    val liquibaseConfOpt = config.getConfig("liquibase")
    val urlOpt = liquibaseConfOpt flatMap (_.getString("url"))
    val driverOpt = liquibaseConfOpt flatMap (_.getString("driver"))
    val usernameOpt = liquibaseConfOpt flatMap (_.getString("user"))
    val passwordOpt = liquibaseConfOpt flatMap (_.getString("password"))
    val changelogOpt = liquibaseConfOpt flatMap (_.getString("changelog"))

    for {
      url       <- urlOpt
      username  <- usernameOpt
      password  <- passwordOpt
      driver    <- driverOpt
      changelog <- changelogOpt
      database  = CommandLineUtils.createDatabaseObject(
        environment.classLoader,
        url,
        username,
        password,
        driver,
        null, // defaultCatalogName
        null, // defaultSchemaName
        false, // outputDefaultCatalog,
        false, // outputDefaultSchema,
        null, // databaseClass,
        null, // driverPropertiesFile,
        null, // propertyProviderClass,
        null, // liquibaseCatalogName,
        null // liquibaseSchemaName
      )
      resourceAccessor = new ClassLoaderResourceAccessor(environment.classLoader)
    } yield new Liquibase(changelog, resourceAccessor, database)
  }

  /**
   * Run Liquibase schema upgrade
   *
   * @param tag Optionally tag schema version
   */
  def upgradeSchema (tag: Option[String] = None): Unit = {
    liquibase() match {
      case Some(lb) =>
        log.info("Running liquibase migrations")
        lb.update(new Contexts())
        tag.foreach(t => lb.tag(t))
      case None =>
        log.warn("Liquibase is not configured")
    }
  }

  /**
   * Show pending schema changes in the log but don't run them.
   */
  def showSql(): Unit = {
    liquibase() match {
      case Some(lb) =>
        val writer = new StringWriter()
        lb.update(new Contexts(), writer)
        log.info(writer.toString)
      case None =>
        log.warn("Liquibase is not configured")
    }
  }

  /**
   * Check if there are pending schema changes.
   *
   * @return true if there are changes to be performed
   */
  def needsUpgrade: Boolean = {
    liquibase() match {
      case Some(lb) =>
        val unrunChanges = lb.listUnrunChangeSets(new Contexts(), new LabelExpression()).asScala
        unrunChanges.foreach {
          change => log.warn(s"Unrun changeset: ${change.getId}, s{change.getAuthor}, ${change.getDescription}, ${change.getComments}")
        }
        unrunChanges.nonEmpty
      case None =>
        log.warn("Liquibase is not configured")
        false
    }

  }

  /**
   * Force unlock Liquibase tables
   */
  def unlock(): Unit = {
    liquibase() match {
      case Some(lb) =>
        log.info("Releasing liquibase locks")
        lb.forceReleaseLocks()
      case None =>
        log.warn("Liquibase is not configured")
    }
  }


}
