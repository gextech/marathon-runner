package gex.marathon.jsr223

import groovy.transform.CompileStatic
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngine

@CompileStatic
class MarathonEngineFactory implements ScriptEngineFactory {

  private ScriptEngineFactory nashornFactory

  MarathonEngineFactory() {
    nashornFactory = 
      (ScriptEngineFactory) Class
      .forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory")
      .newInstance()
  }

  @Override
  String getEngineName() {
    "marathon"
  }

  @Override
  String getEngineVersion() {
    "0.1.0"
  }

  @Override
  List<String> getExtensions() {
    nashornFactory.extensions
  }

  @Override
  String getLanguageName() {
    nashornFactory.languageName
  }

  @Override
  String getLanguageVersion() {
    nashornFactory.languageVersion
  }

  @Override
  String getMethodCallSyntax(String obj, String m, String... args) {
    nashornFactory.getMethodCallSyntax(obj, m, args)
  }

  @Override
  List<String> getMimeTypes() {
    nashornFactory.mimeTypes
  }

  @Override
  List<String> getNames() {
    ["marathon", "js", "javascript", "ECMAScript", "ecmascript", "JavaScript"]
  }

  @Override
  String getOutputStatement(String toDisplay) {
    nashornFactory.getOutputStatement(toDisplay)
  }

  @Override
    Object getParameter(String key) {
      switch (key) {
        case ScriptEngine.NAME:
          return "javascript";
        case ScriptEngine.ENGINE:
          return "Gex Marathon";
        case ScriptEngine.ENGINE_VERSION:
          return getEngineVersion()
        default:
          nashornFactory.getParameter(key)
      }
    }

  @Override
  String getProgram(String... statements) {
    nashornFactory.getProgram(statements)
  }

  @Override
  ScriptEngine getScriptEngine() {
    new MarathonScriptEngine(this)
  }

}

