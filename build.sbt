name := "aluminium-trout"

version := "1.0"

scalaVersion := "2.11.8"

javaSource in Compile := baseDirectory.value / "src"
javaSource in Test := baseDirectory.value / "test"

unmanagedResourceDirectories in Compile := Seq()
unmanagedResourceDirectories in Test := Seq()
unmanagedSourceDirectories in Compile := Seq(baseDirectory.value / "src")
unmanagedSourceDirectories in Test := Seq(baseDirectory.value / "test")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.lwjgl" % "lwjgl" % "3.0.0-SNAPSHOT",
  "org.lwjgl" % "lwjgl-platform" % "3.0.0-SNAPSHOT" classifier "natives-windows",
  "org.lwjgl" % "lwjgl-platform" % "3.0.0-SNAPSHOT" classifier "natives-linux",
  "org.lwjgl" % "lwjgl-platform" % "3.0.0-SNAPSHOT" classifier "natives-osx"
)

// Needed to copy the LWJGL native jars to an accessible folder.
retrieveManaged := true

// This is apparently not needed, so I'm assuming that the platform
// jars are correctly resolved, but we'll keep this for future reference.
//javaOptions += s"-Djava.library.path=${baseDirectory.value / "lib_managed" / "jars" / "org.lwjgl" / "lwjgl-platform"}"

// This is super important. If sbt doesn't fork here, the main method
// doesn't actually get invoked in the main thread. This is detected
// by NSUndoManager, which will promptly throw a cryptic assertion
// failure.
// Additionally, if the JVM decides to invoke main in another thread,
// NSUndoManager throws the same assertion failure. This is why we
// also need the -XstartOnFirstThread option.
fork in run := true
javaOptions += "-XstartOnFirstThread"