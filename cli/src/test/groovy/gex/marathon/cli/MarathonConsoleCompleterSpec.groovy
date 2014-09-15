package gex.marathon.cli

import gex.marathon.cli.config.MarathonConsoleCompleter
import spock.lang.Specification

class MarathonConsoleCompleterSpec extends Specification {

  def "Test  console complete candidates "() {
    when:
      def m = new MarathonConsoleCompleter()
      def candidate = m.getCompletionCandidate(input)

    then:
      candidate == expectedCandidate

    where:
      input       || expectedCandidate
      ':'         || null
      ':qui'      || ':quit'
      ':prom'     || ':prompt > '
      ':settings' || ':settings'
  }

}
