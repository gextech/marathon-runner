package gex.marathon.jsr223

import gex.marathon.core.MarathonRunner

import javax.script.AbstractScriptEngine
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory

class MarathonScriptEngine extends AbstractScriptEngine {

  private MarathonEngineFactory factory

  MarathonScriptEngine(MarathonEngineFactory factory) {
    super()
    this.factory = factory
  }

  Object eval(String code, ScriptContext context) {
  }

  Object eval(Reader reader, ScriptContext context) {
  }

  ScriptEngineFactory getFactory() {
    factory
  }

  Bindings createBindings() {
  }

}

