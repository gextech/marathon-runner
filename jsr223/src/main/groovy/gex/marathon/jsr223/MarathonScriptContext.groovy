package gex.marathon.jsr223

import javax.script.ScriptContext
import gex.marathon.core.MarathonContext
import gex.marathon.module.MarathonModuleLoader

class MarathonScriptContext extends MarathonContext {
  public static final String MARATHON_PATH = "__MARATHON_PATH__"

  private ScriptContext scriptContext

  MarathonScriptContext(ScriptContext scriptContext, MarathonModuleLoader loader) {
    this.scriptContext = scriptContext
    this.loader = loader
  }

  @Override
  void put(String name, Object local) {
    scriptContext.setAttribute(name, local, ScriptContext.ENGINE_SCOPE)
  }

  @Override
  Object get(String name) {
    scriptContext.getAttribute(name, ScriptContext.ENGINE_SCOPE)
  }

  @Override
  Map getLocals() {
    scriptContext.getBindings(ScriptContext.ENGINE_SCOPE)
  }

  void setWriter(PrintWriter writer) {
    scriptContext.writer = writer
  }

  PrintWriter getWriter() {
    scriptContext.writer
  }

  void setErrorWriter(PrintWriter writer) {
    scriptContext.errorWriter = writer
  }

  PrintWriter getErrorWriter() {
    scriptContext.errorWriter
  }
  
}
