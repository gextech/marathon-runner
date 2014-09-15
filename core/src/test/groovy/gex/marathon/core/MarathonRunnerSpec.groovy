package gex.marathon.core

import spock.lang.*

class MarathonRunnerSpec extends Specification {
  def "We can eval simple values"() {
    when:
      def runner = new MarathonRunner()
      def a = runner.eval("5 + 5")

    then:
      a == 10
  }

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

  def "Testing marathon runner with options map constructor"() {
    when:
      def runner = new MarathonRunner([marathonPath: ["src/test/resources/node_modules"] ])
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

  def "Test default modules are correctly loaded"() {
    when:
      def runner = new MarathonRunner(["src/test/resources/node_modules"])
      runner.eval("""
      var fs = require('fs');
      """)
    then:
      runner.get('fs') != null
  }

  def "Test when default modules are disabled (empty array)"() {
    when:
      def runner = new MarathonRunner(["src/test/resources/node_modules"], [])
      runner.eval("""
      var fs = require('fs');
      """)
    then:
      IllegalArgumentException e = thrown()
      e.message == 'Module fs cannot be loaded'
  }

  def "Test when default modules are custom located and loaded"() {
    when:
      def runner = new MarathonRunner(["src/test/resources/node_modules"],
        [[name:'fs', path:'src/test/resources/other_path/modules']])
      runner.eval("""
      var fs = require('fs');
      """)
    then:
      runner.get('fs') != null
  }

  def "We can follow up various steps, testing cli mode"(){
    given:
      def runner = new MarathonRunner()
      def x
    when:
      x = runner.eval("var a = 5 + 5")
    then:
      x == null

    when:
      x = runner.eval("a")
    then:
      x == 10
  }






}

