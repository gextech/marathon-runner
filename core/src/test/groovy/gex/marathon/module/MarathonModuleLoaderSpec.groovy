package gex.marathon.module

import gex.marathon.core.*
import spock.lang.*

class MarathonModuleLoaderSpec extends Specification {
  def "Can load a json file"() {
    when:
      def engine = new MarathonCoreEngine()
      def loader = new MarathonModuleLoader(engine, ["src/test/resources/node_modules/"])
      def testModule = loader.require("coffee-script/test")

    then:
      testModule.exports.test
  }
  
  def "Can load a simple js file"() {
    when:
      def engine = new MarathonCoreEngine()
      def loader = new MarathonModuleLoader(engine, ["src/test/resources/node_modules/"])
      def lodash = loader.require("lodash/dist/lodash.js")

    then:
      lodash
  }

  def "Can load coffee-script"() {
    when:
      def engine = new MarathonCoreEngine()
      def loader = new MarathonModuleLoader(engine, ["src/test/resources/node_modules/"])
      def coffee = loader.require("coffee-script/register")

    then:
      coffee
  }
}

