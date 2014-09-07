package gex.marathon.core

import gex.marathon.module.MarathonModuleLoader
import spock.lang.*

class MarathonCoreEngineSpec extends Specification {
  def "Can execute very simple scripts"() {
    when:
      def engine = new MarathonCoreEngine()
      def val = engine.eval("5+5")

    then:
      val == 10
  }

  def "We can check type of arrays"() {
    when:
      def context = new MarathonContext(scriptName: 'something.js')
      def engine = new MarathonCoreEngine()
      def val = engine.eval("[] instanceof Array", context)

    then:
      val == true
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

  def "We can capture output"() {
    when:
      def strWriter = new StringWriter()
      def context = new MarathonContext(scriptName: 'something.js',
        writer: new PrintWriter(strWriter))
      def engine = new MarathonCoreEngine()
      engine.eval("console.log('Hola mundo')", context)

    then:
      strWriter.toString().trim() == 'Hola mundo'
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

  def "We can load and then use things"() {
    when:
      def loader = new MarathonModuleLoader(['src/test/resources/lodash.jar'])
      def context = new MarathonContext(
        scriptName: 'something.js',
        loader: loader)
      def engine = new MarathonCoreEngine()
      def code = """
      (function () {
        var _ = require('lodash/lodash');
        return {
          every: _.every([true, 1, null, 'yes']),
          filter: _.filter([1, 2, 3, 4, 5, 6], function(num) { return num % 2 == 0; })
        };
      })();
      """
      def val = engine.eval(code, context)

    then:
      val['every'] == false
      val['filter'][0] == 2
      val['filter'][1] == 4
      val['filter'][2] == 6
  }

  def "We can load and then use things"() {
    when:
      def loader = new MarathonModuleLoader(['src/test/resources/node_modules'])
      def context = new MarathonContext(
        scriptName: 'something.js',
        loader: loader)
      def engine = new MarathonCoreEngine()
      def code = """
      (function () {
        require('test/index');
      })();
      """
      engine.eval(code, context)

    then:
      false
  }

}
