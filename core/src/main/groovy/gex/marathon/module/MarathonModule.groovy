package gex.marathon.module

import groovy.transform.CompileStatic

@CompileStatic
class MarathonModule implements GroovyInterceptable {

  Map moduleMap
  String name

  MarathonModule(name, Map exports = [:]) {
    this.name = name
    moduleMap = [
      exports: exports
    ]
  }

  Object getExports() {
    moduleMap.exports
  }

}

