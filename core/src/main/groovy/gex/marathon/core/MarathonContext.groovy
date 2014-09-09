package gex.marathon.core

import groovy.transform.CompileStatic
import gex.marathon.path.MarathonPathResource

import gex.marathon.module.MarathonModuleLoader
import gex.marathon.module.MarathonModule

@CompileStatic
class MarathonContext {
  private Map locals = [:]
  String scriptName
  String scriptPath

  MarathonCoreEngine engine
  MarathonPathResource scriptResource
  MarathonModuleLoader loader
  MarathonModule module

  PrintWriter writer
  PrintWriter errorWriter

  void put(String name, Object local) {
    locals.put(name, local)
  }

  Object get(String name) {
    locals.get(name)
  }

  Map getLocals() {
    locals
  }
}
