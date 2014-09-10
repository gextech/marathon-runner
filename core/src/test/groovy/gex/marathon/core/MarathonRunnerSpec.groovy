package gex.marathon.core

import gex.marathon.module.MarathonModuleLoader
import spock.lang.*

class MarathonRunnerSpec extends Specification {
  def "We can execute by creating a runner"() {
    when:
      def runner = new MarathonRunner()
      runner.eval("var a = 5 + 5")

    then:
      runner.get("a") == 10
  }

  def "We can require and use requirements :P"() {
    when:
      def runner = new MarathonRunner(["src/test/resources/node_modules"])
      runner.eval("""
      var _ = require('lodash/lodash'),
          a = _.map([1, 2, 3], function(n) { return n * 3; });
      """)
      def a = runner.get("a")

    then:
      a[0] == 3
      a[1] == 6
      a[2] == 9
  }

}

