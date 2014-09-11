package gex.marathon.path

import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Files

import groovy.transform.CompileStatic

@CompileStatic
class MarathonPathResource {
  Path path
  Path originPath
  Map originAttributes

  InputStream getInputStream() {
    Files.newInputStream(path)
  }

  String getUtf8Contents() {
    Files.readAllLines(path).join("\n")
  }

  boolean isDirectory() {
    Files.isDirectory(path)
  }

  byte[] getByteContents() {
    Files.readAllBytes(path)
  }

  String getStringContents(String charset) {
    Files.readAllLines(path, Charset.forName(charset)).join("\n")
  }
}
