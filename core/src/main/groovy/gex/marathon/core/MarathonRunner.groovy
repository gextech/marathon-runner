package gex.marathon.core

import gex.marathon.module.MarathonModuleLoader

class MarathonRunner {
  private MarathonCoreEngine engine
  private MarathonModuleLoader loader
  MarathonContext context

  MarathonRunner(List<String> marathonPath = []) {
    engine = new MarathonCoreEngine()
    loader = new MarathonModuleLoader(engine, marathonPath)
    context = new MarathonContext(loader: loader, scriptName: '<user>')
  }

  Object eval(String code) {
    engine.evalGlobal(code, context)
  }

  Object invokeFunction(String function, Object... params) {
    engine.invokeFunction(context, function, params);
  }

  Object invokeMethod(String thizName, String methodName, Object... params) {
    Object thiz = engine.eval(thizName, context)
    engine.invokeMethod(context, thiz, methodName, params)
  }

  MarathonModuleLoader getLoader() {
    loader
  }

  MarathonCoreEngine getEngine() {
    engine
  }

  List<String> getMarathonPath() {
    loader.marathonPath
  }

  void put(String key, Object value) {
    engine.put(key, value)
  }
  
  Object get(String key) {
    engine.get(key)
  }

}
