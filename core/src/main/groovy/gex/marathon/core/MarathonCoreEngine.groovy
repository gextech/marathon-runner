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

  private List<Map> contextStack

  private ScriptEngine scriptEngine
  private Bindings commonBindings
  private ResourceLoader resourceLoader

  MarathonCoreEngine() {
    ScriptEngineManager engineManager = new ScriptEngineManager() 
    this.scriptEngine = engineManager.getEngineByName("nashorn")
    contextStack = new ArrayList()
    resourceLoader = new ResourceLoader()
    loadDefault()

  }

  private void loadDefault() {
    List<String> defaults = ["/marathon/init/shim.js"]
    defaults.each {
      scriptEngine.eval(resourceLoader.getInputStream(it).text)
    }
    commonBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE)
  }

  private void pushStack() {
    Map currentStack = new HashMap()
    ScriptContext currentContext = scriptEngine.context

    currentStack.put("__writer", currentContext.writer)
    currentStack.put("__errorWriter", currentContext.errorWriter)

    currentContext.getBindings(ScriptContext.ENGINE_SCOPE).each { k, v ->
      currentStack.put(k, v)
    }

    contextStack.push(currentStack)
  }

  private void popStack() {
    Map previousStack = contextStack.pop()

    def writer = previousStack.get("__writer")
    def errorWriter = previousStack.get("__errorWriter")

    previousStack.remove("__writer")
    previousStack.remove("__errorWriter")

    previousStack.each { k, v ->
      scriptEngine.context.setAttribute(
        k.toString(),
        v,
        ScriptContext.ENGINE_SCOPE)
    }

  }

  Object invokeFunction(MarathonContext context, String functionName, Object... params) {
    Map marathonGlobal = [
      context: context
    ]
    pushStack()

    try {
      loadLocals(scriptEngine, context)
      Invocable invocable = (Invocable)scriptEngine
      invocable.invokeFunction(functionName, params)
    } finally {
      readLocals(scriptEngine, context)
      popStack()
    }
  }

  Object invokeMethod(MarathonContext context, Object thiz, String methodName, Object... params) {
    Map marathonGlobal = [
      context: context
    ]
    pushStack()

    try {
      loadLocals(scriptEngine, context)
      Invocable invocable = (Invocable)scriptEngine
      invocable.invokeMethod(thiz, methodName, params)
    } finally {
      readLocals(scriptEngine, context)
      popStack()
    }
  }

  Object evalGlobal(String code, MarathonContext context = new MarathonContext()) {
    Map marathonGlobal = [
      context: context
    ]

    def retValue

    ScriptContext engineContext = new SimpleScriptContext()
    engineContext = scriptEngine.context
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
      scriptEngine.eval(getRequireCode())

      retValue = scriptEngine.eval(code)

      scriptEngine.getContext().setAttribute("require", null, ScriptContext.ENGINE_SCOPE)
    }
    finally {
      readLocals(scriptEngine, context)
    }

    retValue
  }



  Object eval(String code, MarathonContext context = new MarathonContext()) {
    Map marathonGlobal = [
      context: context
    ]

    ScriptContext engineContext = new SimpleScriptContext()
    engineContext = scriptEngine.context
    engineContext.setBindings(commonBindings, ScriptContext.ENGINE_SCOPE)
    pushStack()

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
      scriptEngine.eval(formattedCode)
    } finally {
      readLocals(scriptEngine, context)
      popStack()
    }
  }

  void evalModule(String code, MarathonContext context) {
    Map marathonGlobal = [
      context: context
    ]

    ScriptContext engineContext = new SimpleScriptContext()
    engineContext = scriptEngine.context
    engineContext.setBindings(commonBindings, ScriptContext.ENGINE_SCOPE)

    pushStack();


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
      scriptEngine.eval(formattedCode)
      context.module.moduleMap = (Map)engineContext.getAttribute(MARATHON_MODULE, ScriptContext.ENGINE_SCOPE)
    } finally {
      readLocals(scriptEngine, context)
      popStack();
    }
  }

  private void loadLocals(ScriptEngine engine, MarathonContext context) {
    context.locals.each { k, v ->
      engine.context.setAttribute(k.toString(), v, ScriptContext.ENGINE_SCOPE)
    }
  }

  public void readLocals(ScriptEngine engine, MarathonContext context) {
    def map = engine.context.getBindings(ScriptContext.ENGINE_SCOPE)
    def keys = map.keySet()
    keys.each { k ->
      context.put(
        k.toString(),
        map.get(k.toString()))
    }
  }


  private String getRequireCode(){
    String require = resourceLoader.getInputStream("/marathon/module/globalRequire.js").text.replaceAll("\n", " ")
    require
  }


  private String prepareEvalCode(String code) {
    String prefix = resourceLoader.getInputStream("/marathon/module/prefix.js").text.replaceAll("\n", " ")
    String suffix = resourceLoader.getInputStream("/marathon/module/evalSuffix.js").text
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(" return ")
    builder.append(code.replaceAll("^\\s+",""))
    builder.append(suffix)
    builder.toString()
  }

  private String prepareCode(String code) {
    String prefix = resourceLoader.getInputStream("/marathon/module/prefix.js").text.replaceAll("\n", " ")
    String suffix = resourceLoader.getInputStream("/marathon/module/suffix.js").text
    StringBuilder builder = new StringBuilder()
    builder.append(prefix)
    builder.append(code)
    builder.append(suffix)
    builder.toString()
  }

  void put(String name, Object value) {
    scriptEngine.context.setAttribute(name, value, ScriptContext.ENGINE_SCOPE)
  }

  Object get(String name) {
    scriptEngine.context.getAttribute(name)
  }

}

