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
      lodash.type == MarathonResourceType.PATH_PARENT
  }

  def "We can load paths from jars"() {
    given:
      def marathonPath = new MarathonPathReader()
    when:
      marathonPath.addPath("src/test/resources/lodash.jar")
      def lodash = marathonPath.resolvePath("lodash/lodash")
    then:
      lodash != null
  }

  def "We cannot load paths outside from marathon path"() {
    given:
      def marathonPath = new MarathonPathReader()
    when:
      marathonPath.addPath("src/test/resources/node_modules/lodash/dist/")
      marathonPath.resolvePath("../lodash")
    then:
      SecurityException ex = thrown()
  }

}

