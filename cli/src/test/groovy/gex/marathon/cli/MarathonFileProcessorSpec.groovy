package gex.marathon.cli

import spock.lang.Specification

class MarathonFileProcessorSpec extends Specification {

  def "Test file processing"() {

    when:
      def noOptions = [:]
      def mfp = new MarathonFileProcessor(noOptions)

      def jsFile = getClass().getResource('/sample.js').getPath()
      mfp.processFile(jsFile)

    then:
      notThrown Exception
  }

}
