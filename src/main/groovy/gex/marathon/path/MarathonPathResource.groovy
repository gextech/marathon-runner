package gex.marathon.path

import java.nio.file.Path
import java.nio.file.Files

import groovy.transform.CompileStatic

@CompileStatic
class MarathonPathResource {
  Path path
  Path originPath
  Map originAttributes

  String getUtf8Contents() {
    Files.readAllLines(path).join("\n")
  }
}
