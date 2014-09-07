package gex.marathon.core

import spock.lang.*

class MarathonCoreEngineSpec extends Specification {
  def "Can execute very simple scripts"() {
    when:
      def engine = new MarathonCoreEngine()
      def val = engine.eval("5+5")

    then:
      val == 10
  }

  def "We get the correct filename in exceptions"() {
    when:
      def context = new MarathonContext(scriptName: 'something.js')
      def engine = new MarathonCoreEngine()
      def val = engine.eval("5++5", context)

    then:
      javax.script.ScriptException ex = thrown()
      ex.fileName == "something.js"
  }

  def "Scripts know their current filename"() {
    when:
      def context = new MarathonContext(scriptName: 'something.js')
      def engine = new MarathonCoreEngine()
      def val = engine.eval("__filename", context)

    then:
      val == "something.js"
  }

  def "We can plain eval json objects"() {
    when:
      def engine = new MarathonCoreEngine()
      def val = engine.eval('{5:6,"uno":2}')

    then:
      val[5] == 6
      val["uno"] == 2
  }

}
