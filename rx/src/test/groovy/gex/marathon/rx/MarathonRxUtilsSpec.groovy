package gex.marathon.rx

import spock.lang.*
import rx.Observable
import gex.marathon.core.MarathonRunner

class MarathonRxUtilsSpec extends Specification {

  def "We can convert a java observable into js"() {
    when:
      Observable oneObservable = Observable.just(1)
      def runner = new MarathonRunner()
      def jsObservable = MarathonRxUtils.fromJava(runner, oneObservable)

    then:
      false
  }

}

