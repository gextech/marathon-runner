package gex.marathon.core

import groovy.transform.CompileStatic
import gex.marathon.path.MarathonPathResource

import gex.marathon.module.MarathonModuleLoader
import gex.marathon.module.MarathonModule

@CompileStatic
class MarathonContext {
  String scriptName
  String scriptPath

  MarathonPathResource scriptResource
  MarathonModuleLoader loader
  MarathonModule module

  PrintWriter writer
  PrintWriter errorWriter
}
