package gex.marathon.jsr223

import javax.script.ScriptContext
import gex.marathon.core.MarathonContext

class MarathonScriptContext extends MarathonContext {
  private ScriptContext scriptContext

  MarathonScriptContext(ScriptContext scriptContext) {
    this.scriptContext = scriptContext
  }

  @Override
  void put(String name, Object local) {
    scriptContext.setAttribute(name, local, ScriptContext.ENGINE_SCOPE)
  }

  @Override
  Object get(String name) {
    scriptContext.getAttribute(name, local)
  }

  @Override
  Map getLocals() {
    scriptContext.getBindings(ScriptContext.ENGINE_SCOPE)
  }
  
}
