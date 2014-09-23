package gex.marathon.path

import spock.lang.*

class MarathonPathReaderSpec extends Specification {

  def "We can load paths from folders"() {
    given:
      def marathonPath = new MarathonPathReader()
    when:
      marathonPath.addPath("src/test/resources/node_modules/")
      def lodash = marathonPath.resolvePath("lodash/lodash")
    then:
      lodash != null
  }

  def "We can load relative paths"() {
    given:
      def rootPath = new MarathonPathReader()
      rootPath.addPath("src/test/resources/node_modules/")
      def coffee = rootPath.resolvePath("coffee-script/register.js")
      def marathonPath = new MarathonPathReader(coffee)

    when:
      def mkdirp = marathonPath.resolvePath("mkdirp/index")

    then:
      mkdirp != null
  }

  def "We can load paths from jars"() {
    given:
      def marathonPath = new MarathonPathReader()
    when:
      marathonPath.addPath("src/test/resources/lodash.jar")
      def lodash = marathonPath.resolvePath("lodash")
    then:
      lodash != null
  }

  def "We can load relative paths from jars"() {
    given:
      def rootPath = new MarathonPathReader()
      rootPath.addPath("src/test/resources/coffee-script.jar")
      def coffee = rootPath.resolvePath("coffee-script/register.js")
      def marathonPath = new MarathonPathReader(coffee)

    when:
      def mkdirp = marathonPath.resolvePath("mkdirp/index")

    then:
      mkdirp != null
  }

  def "We can load json files"() {
    given:
      def marathonPath = new MarathonPathReader()
    when:
      marathonPath.addPath("src/test/resources/node_modules/")
      def testJson = marathonPath.resolvePath("coffee-script/test")
    then:
      testJson != null
  }

  def "We can load relative json paths from fs"() {
    given:
      def rootPath = new MarathonPathReader()
      rootPath.addPath("src/test/resources/node_modules")
      def coffee = rootPath.resolvePath("coffee-script/register.js")
      def marathonPath = new MarathonPathReader(coffee)

    when:
      def testJson = marathonPath.resolvePath("./test.json")

    then:
      testJson != null
  }

  def "We can load relative json paths from jars"() {
    given:
      def rootPath = new MarathonPathReader()
      rootPath.addPath("src/test/resources/coffee-script.jar")
      def coffee = rootPath.resolvePath("coffee-script/register.js")
      def marathonPath = new MarathonPathReader(coffee)

    when:
      def testJson = marathonPath.resolvePath("./test")

    then:
      testJson != null
  }

}

