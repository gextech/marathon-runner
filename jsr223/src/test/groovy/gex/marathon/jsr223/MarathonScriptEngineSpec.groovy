package gex.marathon.jsr223

import spock.lang.*
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext
import javax.script.ScriptContext

class MarathonScriptEngineSpec extends Specification {
  def "We can eval js code"() {
    when:
      def manager = new ScriptEngineManager()
      def engine = manager.getEngineByName("marathon")
      def sum = engine.eval("5 + 5")

    then:
      sum == 10
  }

  def "We can execute and check context"() {
    when:
      def manager = new ScriptEngineManager()
      def engine = manager.getEngineByName("marathon")
      engine.eval("var a = 5 + 1")
      def sum = engine.context.getAttribute("a", ScriptContext.ENGINE_SCOPE)

    then:
      sum == 6
  }

  def "We can execute and then check the value"() {
    when:
      def manager = new ScriptEngineManager()
      def engine = manager.getEngineByName("marathon")
      engine.eval("var a = 5 + 1")
      def sum = engine.eval("a")

    then:
      sum == 6
  }

  def "We can execute with a different context"() {
    when:
      def manager = new ScriptEngineManager()
      def context = new SimpleScriptContext()
      def engine = manager.getEngineByName("marathon")
      engine.eval("var a = 5 + 1", context)
      def sum = context.getAttribute("a", ScriptContext.ENGINE_SCOPE)

    then:
      sum == 6
  }

  def "We can execute with a different context without polluting context"() {
    when:
      def manager = new ScriptEngineManager()
      def context = new SimpleScriptContext()
      def engine = manager.getEngineByName("marathon")
      engine.eval("var a = 5 + 1", context)
      def sum = engine.context.getAttribute("a", ScriptContext.ENGINE_SCOPE)

    then:
      sum == null
  }

  def "We can load with path in node modules"() {
    when:
      def manager = new ScriptEngineManager()
      def context = new SimpleScriptContext()
      context.setAttribute("__MARATHON_PATH__", ["../core/src/test/resources/node_modules"], ScriptContext.ENGINE_SCOPE)
      def engine = manager.getEngineByName("marathon")
      engine.eval("var _ = require('lodash')", context)
      def test = engine.eval("_.range(4)")

    then:
      test[0] == 0
      test[1] == 1
      test[2] == 2
      test[3] == 3
  }

}
