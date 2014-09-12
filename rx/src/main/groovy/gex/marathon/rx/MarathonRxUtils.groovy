package gex.marathon.rx

import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonContext
import gex.marathon.core.MarathonUtils
import rx.Observable

class MarathonRxUtils {

  static Object fromJava(MarathonRunner runner, Observable javaObservable) {
    def context = new MarathonContext(loader: runner.loader)
    def converterCode = MarathonUtils.readResource("/marathon/converters/rx.js")
    runner.eval(converterCode, context)
    runner.invokeFunction(context, "fromJavaRx", javaObservable)
  }

  static Observable toJava(MarathonRunner runner, Object jsObservable) {
    def context = new MarathonContext(loader: runner.loader)
    def converterCode = MarathonUtils.readResource("/marathon/converters/rx.js")
    runner.eval(converterCode, context)
    runner.invokeFunction(context, "toJavaRx", jsObservable)
  }
}
