package gex.marathon.core

import gex.marathon.module.MarathonModule
import gex.marathon.module.MarathonModuleLoader

class MarathonRunner {
  private MarathonCoreEngine engine
  private MarathonModuleLoader loader
  MarathonContext context
  Map options

  MarathonRunner(List<String> marathonPath = [], List<Map> initialModules = null) {
    this.options = [marathonPath: marathonPath, initModules: initialModules]
    buildMarathonRunner(marathonPath, initialModules)
  }

  MarathonRunner(Map options){
    if(!options.marathonPath){
      options.marathonPath = []
    }
    this.options = options
    buildMarathonRunner(options.marathonPath, options.initModules)
  }

  private buildMarathonRunner(List<String> marathonPath = [], List<Map> initialModules = null){
    engine = new MarathonCoreEngine()
    loader = new MarathonModuleLoader(engine, marathonPath, null, false, initialModules)
    context = new MarathonContext(loader: loader, scriptName: '<user>')
  }

  MarathonContext evalModule(String moduleName, String code) {
    MarathonContext context = new MarathonContext(loader: loader, scriptName: moduleName)

    context.module = new MarathonModule(moduleName)
    engine.evalModule(code, context)

    context
  }

  Object eval(String code) {
    engine.evalGlobal(code, context)
  }

  Object eval(String code, MarathonContext context) {
    engine.evalGlobal(code, context)
  }

  Object invokeFunction(MarathonContext context, String function, Object... params) {
    engine.invokeFunction(context, function, params);
  }

  Object invokeMethod(MarathonContext context, String thizName, String methodName, Object... params) {
    Object thiz = engine.eval(thizName, context)
    engine.invokeMethod(context, thiz, methodName, params)
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
