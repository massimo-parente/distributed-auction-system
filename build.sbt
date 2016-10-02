name := """fantapesce distributed auction system"""

version := "0.0.1-alpha.1"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)


scalaVersion := "2.11.8"

incOptions := incOptions.value.withNameHashing(true)

updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

libraryDependencies ++= {

  Seq(
    cache,
    ws,
    evolutions,

    "com.typesafe.akka" %% "akka-actor" % "2.4.9",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.9",
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
    "com.h2database" % "h2" % "1.4.192",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.9" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test",

    "org.reactivecouchbase" %% "reactivecouchbase-play" % "0.3",

    "org.webjars" % "bootstrap" % "3.3.7",
    "org.webjars" % "flot" % "0.8.0",
    "org.webjars.npm" % "angular__common" % "2.0.1",
    "org.webjars.npm" % "angular__compiler" % "2.0.1",
    "org.webjars.npm" % "angular__core" % "2.0.1",
    "org.webjars.npm" % "angular__platform-browser-dynamic" % "2.0.1",
    "org.webjars.npm" % "angular__platform-browser" % "2.0.1",
    "org.webjars.npm" % "angular__http" % "2.0.1",
    "org.webjars.npm" % "angular__router" % "3.0.1",
    "org.webjars.npm" % "angular__forms" % "2.0.1",
    "org.webjars.npm" % "systemjs" % "0.19.39",
    "org.webjars.npm" % "todomvc-common" % "1.0.2",
    "org.webjars.npm" % "rxjs" % "5.0.0-beta.12",
    "org.webjars.npm" % "es6-promise" % "3.1.2",
    "org.webjars.npm" % "es6-shim" % "0.35.1",
    "org.webjars.npm" % "reflect-metadata" % "0.1.8",
    "org.webjars.npm" % "zone.js" % "0.6.25",
    "org.webjars.npm" % "core-js" % "2.4.1",
    "org.webjars.npm" % "symbol-observable" % "1.0.1",
    "org.webjars.npm" % "typescript" % "2.0.2",

    //tslint dependency
    "org.webjars.npm" % "tslint-eslint-rules" % "1.2.0",
    "org.webjars.npm" % "codelyzer" % "0.0.25",
    "org.webjars.npm" % "types__jasmine" % "2.2.26-alpha" % "test"

    //test
    //  "org.webjars.npm" % "jasmine-core" % "2.4.1"
  )
}

resolvers += "ReactiveCouchbase" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases"

dependencyOverrides += "org.webjars.npm" % "minimatch" % "3.0.0"

// the typescript typing information is by convention in the typings directory
// It provides ES6 implementations. This is required when compiling to ES5.
typingsFile := Some(baseDirectory.value / "typings" / "index.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules plus ng2 lint rules
//(rulesDirectories in tslint) := Some(List(
//  tslintEslintRulesDir.value,
//  ng2LintRulesDir.value
//))

routesGenerator := InjectedRoutesGenerator
