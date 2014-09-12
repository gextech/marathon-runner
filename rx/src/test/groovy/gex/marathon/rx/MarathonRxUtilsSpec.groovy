package gex.marathon.rx

import spock.lang.*
import rx.Observable
import gex.marathon.core.MarathonRunner

import spock.util.concurrent.PollingConditions

class MarathonRxUtilsSpec extends Specification {

  def "We can convert a java observable into js"() {
    when:
      Observable oneObservable = Observable.just(42)
      def conditions = new PollingConditions(timeout: 5)
      def runner = new MarathonRunner(['src/test/resources/node_modules'])

      def jsObservable = MarathonRxUtils.fromJava(runner, oneObservable)
      runner.put("theObservable", jsObservable)

      runner.eval("""
      var a = 0;
      theObservable.subscribe(
      function (x) {
        a = x;
        console.log("Next", x);
      },
      function (err) {
        console.log("Error", err);
      },
      function () {
        console.log('Completed');
      })
      """)

    then:
      conditions.eventually {
        runner.eval("a") == 42
      }
  }

  def "We can convert a js observable into java"() {
    when:
      def conditions = new PollingConditions(timeout: 5)
      def runner = new MarathonRunner(['src/test/resources/node_modules'])
      def observable = runner.eval("require('rx/dist/rx.all').Observable.return(42)")
      def javaObservable = MarathonRxUtils.toJava(runner, observable)

    then:
      javaObservable.toBlocking().first() == 42
  }

}

