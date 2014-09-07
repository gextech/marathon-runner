package gex.marathon.module

import groovy.transform.CompileStatic

@CompileStatic
class MarathonModule {

  private Map moduleMap
  String name

  MarathonModule(name, Map exports = [:]) {
    this.name = name
    moduleMap = [
      exports: exports
    ]
  }

  Map getModuleMap() {
    moduleMap
  }

  Object getExports() {
    moduleMap.exports
  }
}
