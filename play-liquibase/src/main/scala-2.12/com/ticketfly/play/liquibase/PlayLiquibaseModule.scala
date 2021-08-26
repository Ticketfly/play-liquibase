package com.ticketfly.play.liquibase

import java.io.StringWriter
import java.net.URLClassLoader

import javax.inject.{Inject, Singleton}
import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.{ClassLoaderResourceAccessor, FileSystemResourceAccessor}
import liquibase.servicelocator.ServiceLocator
import liquibase.{Contexts, LabelExpression, Liquibase}
import play.api._
import play.api.db.{DBApi, DatabaseConfig, DefaultDatabase}
import play.api.inject._

import scala.language.implicitConversions
import scala.collection.JavaConverters._
import scala.util.Try

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

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    // eagerly bind module so it runs on Play startup
    bind[PlayLiquibase].toSelf.eagerly()
  )
}

@Singleton
class PlayLiquibase @Inject()(environment: Environment, config: Configuration, injector: Injector) {
  private val log = Logger(classOf[PlayLiquibase])
  val enableLiquibase: Boolean = config.getOptional[Boolean](Param.liquibase + "." + Param.enable).getOrElse(true)
  if (enableLiquibase) {
    // Constructor
    upgradeSchema(config.getOptional[String]("app.version"))
  } else {
    log.info("Liquibase is disabled. No migrations will be run")
  }

  lazy val contexts: Contexts = config.getOptional[Seq[String]](Param.liquibase + ".contexts").fold(new Contexts())(l
  => new Contexts(l.asJava))

  /**
   * Run Liquibase schema upgrade
   *
   * @param tag Optionally tag schema version
   */
  def upgradeSchema(tag: Option[String] = None): Unit =
    liquibase() foreach {
      lb =>
        log.info("Running liquibase migrations")
        lb.update(contexts)
        tag.foreach(t => lb.tag(t))
    }

  /**
   * Show pending schema changes in the log but don't run them.
   */
  def showSql(): Unit = liquibase().foreach {
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
  def needsUpgrade: Boolean = liquibase().exists {
    lb =>
      val unrunChanges = lb.listUnrunChangeSets(contexts, new LabelExpression()).asScala
      unrunChanges.foreach {
        change => log.warn(s"Unrun changeset: ${change.getId}, ${change.getAuthor}, ${change.getDescription}, ${change.getComments}")
      }
      unrunChanges.nonEmpty
  }

  /**
   * Force unlock Liquibase tables
   */
  def unlock(): Unit = liquibase().foreach {
    lb =>
      log.info("Releasing liquibase locks")
      lb.forceReleaseLocks()
  }

  type Param = Param.Value

  object Param extends Enumeration {
    val contexts,
    enable,
    changelog,
    url,
    driver,
    user,
    password,
    defaultCatalogName,
    defaultSchemaName,
    databaseClass,
    driverPropertiesFile,
    propertyProviderClass,
    liquibaseCatalogName,
    liquibaseSchemaName,
    databaseChangeLogTableName,
    databaseChangeLogLockTableName = Value

    implicit def toStr(v: Param): String = v.toString

    val classpathPrefix = "classpath:"
    val liquibase = "liquibase"
  }

  /** get p parameter from cfg.
   * If not found use defValue
   *
   * @param p        parameter get value for
   * @param defValue default value
   * @param cfg      configuration the value shall be taken from
   * @return null if not set or trimmed to an empty string (null is required for liquibase)
   */
  def getParam(p: Param, defValue: Option[String] = None)
              (implicit cfg: Option[Configuration]): String = {
    cfg.flatMap(_.getOptional[String](p))
      .orElse(defValue)
      .collect { case x if x.trim.nonEmpty => x }
      .orNull
  }

  protected def liquibase(): Option[Liquibase] = {
    var missingRequiredParam = false

    /** get p parameter from cfg.
     * If not found use defValue
     *
     * @param p        parameter get value for
     * @param defValue default value
     * @param cfg      configuration the value shall be taken from
     * @return null if not set or trimmed to an empty string
     */
    def getRequiredParam(p: Param, defValue: Option[String] = None)
                        (implicit cfg: Option[Configuration]): String = {
      val ret = getParam(p, defValue)(cfg)
      if (null == ret) {
        missingRequiredParam = true
        log.warn(s"Missing required configuration parameter: $p")
      }
      ret
    }

    implicit val liquibaseConfOpt: Option[Configuration] = config.getOptional[Configuration]("liquibase")

    val dbCfg = singleJdbcDatabaseConfig

    val url = getRequiredParam(Param.url, dbCfg.flatMap(_.url))
    val driver = getRequiredParam(Param.driver, dbCfg.flatMap(_.driver))

    if (missingRequiredParam) {
      log.warn("Liquibase is not configured properly! See previous logs")
      None
    }
    else {
      val threadClsLoader = Thread.currentThread.getContextClassLoader

      val urls = Array("liquibase.Liquibase", driver).map {
        Class.forName(_).getProtectionDomain.getCodeSource.getLocation
      }

      val cl = new URLClassLoader(urls, threadClsLoader)
      ServiceLocator.getInstance().setResourceAccessor(new ClassLoaderResourceAccessor(cl))

      val changelog = getParam(Param.changelog, Some("conf/liquibase/changelog.xml"))

      val database = CommandLineUtils.createDatabaseObject(
        new ClassLoaderResourceAccessor(cl),
        url,
        getParam(Param.user, dbCfg.flatMap(_.username)),
        getParam(Param.password, dbCfg.flatMap(_.password)),
        driver,
        getParam(Param.defaultCatalogName),
        getParam(Param.defaultSchemaName),
        false, // outputDefaultCatalog,
        false, // outputDefaultSchema,
        getParam(Param.databaseClass),
        getParam(Param.driverPropertiesFile),
        getParam(Param.propertyProviderClass),
        getParam(Param.liquibaseCatalogName),
        getParam(Param.liquibaseSchemaName),
        getParam(Param.databaseChangeLogTableName),
        getParam(Param.databaseChangeLogLockTableName)
      )

      val resourceAccessor = if (changelog.startsWith(Param.classpathPrefix)) {
        new ClassLoaderResourceAccessor(environment.classLoader)
      } else {
        new FileSystemResourceAccessor(environment.rootPath.getPath)
      }
      Some(new Liquibase(changelog.replaceFirst(Param.classpathPrefix, ""), resourceAccessor, database))
    }
  }

  /** Get the db parameters from the configuration if only one is defined */
  protected def singleJdbcDatabaseConfig: Option[DatabaseConfig] = {
    Try(injector.instanceOf(classOf[DBApi])).toOption.map { dbApi =>
      val dbs = dbApi.databases
      if (dbs.length == 1) {
        dbs.head match {
          case db: DefaultDatabase => db.databaseConfig
          case _ => null
        }
      } else null
    }
  }
}