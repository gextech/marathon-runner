package gex.marathon.core

import gex.marathon.module.MarathonModuleLoader

class MarathonRunner {
  private MarathonCoreEngine engine
  private MarathonModuleLoader loader
  MarathonContext context

  MarathonRunner(List<String> marathonPath = []) {
    engine = new MarathonCoreEngine()
    context = new MarathonContext()
    context.scriptName = '<user>'
    loader = new MarathonModuleLoader(engine, marathonPath)
    context.loader = loader
  }

  Object eval(String code) {
    engine.evalGlobal(code, context)
  }


  MarathonModuleLoader getLoader() {
    loader
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
