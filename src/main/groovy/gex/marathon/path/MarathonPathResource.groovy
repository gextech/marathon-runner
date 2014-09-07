package gex.marathon.path

import java.nio.file.Path

import groovy.transform.CompileStatic

@CompileStatic
class MarathonPathResource {
  Path path
  Path originPath
  Map originAttributes
}
