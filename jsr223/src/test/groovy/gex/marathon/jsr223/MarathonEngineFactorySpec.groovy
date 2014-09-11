package gex.marathon.jsr223

import spock.lang.*
import javax.script.ScriptEngineManager

class MarathonEngineFactorySpec extends Specification {
  def "We can find the marathon engine factory"() {
    when:
      def manager = new ScriptEngineManager()
      def engine = manager.getEngineByName("marathon")

    then:
      engine
  }
}
