package gex.marathon.core

import groovy.transform.CompileStatic
import gex.marathon.path.MarathonPathResource

@CompileStatic
class MarathonContext {
  String scriptName
  String scriptPath

  MarathonPathResource scriptResource
}
