package gex.marathon.rx

import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonContext
import gex.marathon.core.MarathonUtils
import gex.marathon.core.ResourceLoader
import rx.Observable

class MarathonRxUtils {

  ResourceLoader resourceLoader = new ResourceLoader()

  Object fromJava(MarathonRunner runner, Observable javaObservable) {
    def context = new MarathonContext(loader: runner.loader)
    def converterCode = resourceLoader.getInputStream("/marathon/converters/rx.js").text
    runner.eval(converterCode, context)
    runner.invokeFunction(context, "fromJavaRx", javaObservable)
  }

  Observable toJava(MarathonRunner runner, Object jsObservable) {
    def context = new MarathonContext(loader: runner.loader)
    def converterCode = resourceLoader.getInputStream("/marathon/converters/rx.js").text
    runner.eval(converterCode, context)
    runner.invokeFunction(context, "toJavaRx", jsObservable)
  }
}
