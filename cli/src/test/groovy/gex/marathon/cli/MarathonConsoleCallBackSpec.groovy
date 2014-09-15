package gex.marathon.cli

import spock.lang.Specification

class MarathonConsoleCallBackSpec extends Specification {

  def "Test input vs regex"() {

    when:
      def m = new MarathonConsoleCallBack(null, null)
      def match = m.getMatchWithCommand(input)

    then:
      (match == null) == isNull
      if (!isNull) {
        match.name == expected
      }

    where:
      input             || isNull || expected
      'quit'            || false  || 'quit'
      ':quit'           || false  || 'quit'
      ':reload true'    || false  || 'reload'
      ':reload false'   || false  || 'reload'
      ':reload invalid' || true   || 'X'
      ':reload'         || false  || 'getReload'
      ':RELOAD'         || false  || 'getReload'
  }

}
