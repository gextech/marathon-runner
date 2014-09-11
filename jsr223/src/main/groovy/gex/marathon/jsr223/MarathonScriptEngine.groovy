package gex.marathon.jsr223

import groovy.transform.CompileStatic
import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonContext

import javax.script.AbstractScriptEngine
import javax.script.Bindings
import javax.script.SimpleBindings
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

  @Override
  Object eval(String code, ScriptContext context) {
    MarathonContext marathonContext = new MarathonScriptContext(context)
    runner.eval(code, marathonContext)
  }

  @Override
  Object eval(Reader reader, ScriptContext context) {
    eval(reader.text, context)
  }

  @Override
  ScriptEngineFactory getFactory() {
    factory
  }

  @Override
  Bindings createBindings() {
    new SimpleBindings()
  }

}

