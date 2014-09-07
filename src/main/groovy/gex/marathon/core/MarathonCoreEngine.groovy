package gex.marathon.core

import groovy.transform.CompileStatic

import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@CompileStatic
class MarathonCoreEngine {
  public static final String MARATHON_GLOBAL = "__marathon"
  public static final String MARATHON_MODULE = "module"
  public static final String MARATHON_EXPORTS = "exports"

  private ScriptEngine scriptEngine

  MarathonCoreEngine() {
    ScriptEngineManager engineManager = new ScriptEngineManager() 
    this.scriptEngine = engineManager.getEngineByName("javascript")
    loadDefault()
  }

  private void loadDefault() {
    List<String> defaults = ["/marathon/init/shim.js"] 
    defaults.each {
      def source = MarathonUtils.readResource(it)
      scriptEngine.eval(source)
    }
  }

  Object eval(String code, MarathonContext context = new MarathonContext()) {
    Map marathonGlobal = [
      context: context
    ]
    scriptEngine.context.setAttribute(ScriptEngine.FILENAME, context.scriptName, ScriptContext.ENGINE_SCOPE)
    scriptEngine.context.setAttribute(MARATHON_GLOBAL, marathonGlobal, ScriptContext.ENGINE_SCOPE)
    try {
      String formattedCode = prepareEvalCode(code)
      scriptEngine.eval(formattedCode)
    } finally {
      scriptEngine.context.removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE)
      scriptEngine.context.removeAttribute(MARATHON_GLOBAL, ScriptContext.ENGINE_SCOPE)
    }
  }

  void evalModule(String code, MarathonContext context) {
    Map marathonGlobal = [
      context: context
    ]
    scriptEngine.context.setAttribute(ScriptEngine.FILENAME, context.scriptName, ScriptContext.ENGINE_SCOPE)
    scriptEngine.context.setAttribute(MARATHON_GLOBAL, marathonGlobal, ScriptContext.ENGINE_SCOPE)
    scriptEngine.context.setAttribute(MARATHON_MODULE, context.module.moduleMap, ScriptContext.ENGINE_SCOPE)
    scriptEngine.context.setAttribute(MARATHON_EXPORTS, context.module.moduleMap.exports, ScriptContext.ENGINE_SCOPE)
    try {
      String formattedCode = prepareCode(code)
      scriptEngine.eval(formattedCode)
      context.module.moduleMap.exports = scriptEngine.context.getAttribute(MARATHON_EXPORTS, ScriptContext.ENGINE_SCOPE)
    } finally {
      scriptEngine.context.removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE)
      scriptEngine.context.removeAttribute(MARATHON_GLOBAL, ScriptContext.ENGINE_SCOPE)
      scriptEngine.context.removeAttribute(MARATHON_MODULE, ScriptContext.ENGINE_SCOPE)
      scriptEngine.context.removeAttribute(MARATHON_EXPORTS, ScriptContext.ENGINE_SCOPE)
    }
  }

  private String prepareEvalCode(String code) {
    String prefix = MarathonUtils.readResource("/marathon/module/prefix.js").replaceAll("\n", " ")
    String suffix = MarathonUtils.readResource("/marathon/module/evalSuffix.js").replaceAll("\n", " ")
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(" return ")
    builder.append(code)
    builder.append(suffix)
    builder.toString()
  }

  private String prepareCode(String code) {
    String prefix = MarathonUtils.readResource("/marathon/module/prefix.js").replaceAll("\n", " ")
    String suffix = MarathonUtils.readResource("/marathon/module/suffix.js").replaceAll("\n", " ")
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(code)
    builder.append(suffix)
    builder.toString()
  }
}

