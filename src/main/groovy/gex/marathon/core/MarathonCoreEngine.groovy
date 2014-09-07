package gex.marathon.core

import groovy.transform.CompileStatic

import javax.script.Bindings
import javax.script.Invocable
import javax.script.ScriptContext
import javax.script.SimpleScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@CompileStatic
class MarathonCoreEngine {
  public static final String MARATHON_GLOBAL = "__marathon"
  public static final String MARATHON_MODULE = "module"
  public static final String MARATHON_EXPORTS = "exports"

  private ScriptEngine scriptEngine
  private Bindings commonBindings

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
    commonBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE)
  }

  Object invokeFunction(MarathonContext context, String functionName, Object... params) {
    Map marathonGlobal = [
      context: context
    ]
    ScriptContext originalEngineContext = scriptEngine.context
    ScriptContext engineContext = new SimpleScriptContext()
    engineContext.setBindings(commonBindings, ScriptContext.ENGINE_SCOPE)

    if(context.writer) {
      engineContext.writer = context.writer
    }
    if(context.errorWriter) {
      engineContext.errorWriter = context.errorWriter
    }

    engineContext.setAttribute(ScriptEngine.FILENAME, context.scriptName, ScriptContext.ENGINE_SCOPE)
    engineContext.setAttribute(MARATHON_GLOBAL, marathonGlobal, ScriptContext.ENGINE_SCOPE)
    try {
      loadLocals(scriptEngine, context)
      Invocable invocable = (Invocable)scriptEngine
      scriptEngine.setContext(engineContext)
      invocable.invokeFunction(functionName, params)
    } finally {
      readLocals(scriptEngine, context)
      scriptEngine.setContext(originalEngineContext)
    }
  }

  Object eval(String code, MarathonContext context = new MarathonContext()) {
    Map marathonGlobal = [
      context: context
    ]

    ScriptContext engineContext = new SimpleScriptContext()
    engineContext.setBindings(commonBindings, ScriptContext.ENGINE_SCOPE)

    if(context.writer) {
      engineContext.writer = context.writer
    }
    if(context.errorWriter) {
      engineContext.errorWriter = context.errorWriter
    }

    engineContext.setAttribute(ScriptEngine.FILENAME, context.scriptName, ScriptContext.ENGINE_SCOPE)
    engineContext.setAttribute(MARATHON_GLOBAL, marathonGlobal, ScriptContext.ENGINE_SCOPE)
    try {
      loadLocals(scriptEngine, context)
      String formattedCode = prepareEvalCode(code)
      scriptEngine.eval(formattedCode, engineContext)
    } finally {
      readLocals(scriptEngine, context)
    }
  }

  void evalModule(String code, MarathonContext context) {
    Map marathonGlobal = [
      context: context
    ]

    ScriptContext engineContext = new SimpleScriptContext()
    engineContext.setBindings(commonBindings, ScriptContext.ENGINE_SCOPE)

    if(context.writer) {
      engineContext.writer = context.writer
    }
    if(context.errorWriter) {
      engineContext.errorWriter = context.errorWriter
    }

    engineContext.setAttribute(ScriptEngine.FILENAME, context.scriptName, ScriptContext.ENGINE_SCOPE)
    engineContext.setAttribute(MARATHON_GLOBAL, marathonGlobal, ScriptContext.ENGINE_SCOPE)
    engineContext.setAttribute(MARATHON_MODULE, context.module.moduleMap, ScriptContext.ENGINE_SCOPE)
    engineContext.setAttribute(MARATHON_EXPORTS, context.module.moduleMap.exports, ScriptContext.ENGINE_SCOPE)

    try {
      loadLocals(scriptEngine, context)
      String formattedCode = prepareCode(code)
      scriptEngine.eval(formattedCode, engineContext)
      context.module.moduleMap.exports = engineContext.getAttribute(MARATHON_EXPORTS, ScriptContext.ENGINE_SCOPE)
    } finally {
      readLocals(scriptEngine, context)
    }
  }

  private void loadLocals(ScriptEngine engine, MarathonContext context) {
    context.locals.each { k, v ->
      engine.context.setAttribute(k.toString(), v, ScriptContext.ENGINE_SCOPE)
    }
  }

  public void readLocals(ScriptEngine engine, MarathonContext context) {
    def keys = context.locals.keySet()
    keys.each { k ->
      context.put(
        k.toString(),
        engine.context.getAttribute(k.toString(), ScriptContext.ENGINE_SCOPE))
    }
  }

  private String prepareEvalCode(String code) {
    String prefix = MarathonUtils.readResource("/marathon/module/prefix.js").replaceAll("\n", " ")
    String suffix = MarathonUtils.readResource("/marathon/module/evalSuffix.js")
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(" return ")
    builder.append(code.replaceAll("^\\s+",""))
    builder.append(suffix)
    builder.toString()
  }

  private String prepareCode(String code) {
    String prefix = MarathonUtils.readResource("/marathon/module/prefix.js").replaceAll("\n", " ")
    String suffix = MarathonUtils.readResource("/marathon/module/suffix.js")
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(code)
    builder.append(suffix)
    builder.toString()
  }
}

