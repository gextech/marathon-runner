package gex.marathon.jsr223

import groovy.transform.CompileStatic
import gex.marathon.core.MarathonRunner

import javax.script.AbstractScriptEngine
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory

@CompileStatic
class MarathonScriptEngine extends AbstractScriptEngine {

  private MarathonEngineFactory factory
  private MarathonRunner runner

  MarathonScriptEngine(MarathonEngineFactory factory) {
    super()
    this.factory = factory
    this.runner = new MarathonRunner()
  }

  Object eval(String code, ScriptContext context) {
    println context
    runner.eval(code)
  }

  Object eval(Reader reader, ScriptContext context) {
    eval(reader.text, context)
  }

  ScriptEngineFactory getFactory() {
    factory
  }

  Bindings createBindings() {
  }

}

