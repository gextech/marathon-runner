package gex.marathon.path

import java.nio.file.Path
import java.nio.file.FileSystem

import groovy.transform.CompileStatic

enum MarathonResourceType {
  PATH_PARENT,
  FS_PARENT
}

@CompileStatic
class MarathonPathResource {
  Path path
  Path originPath
  FileSystem originFileSystem

  MarathonResourceType getType() {
    if(originPath) {
      return MarathonResourceType.PATH_PARENT
    } else {
      return MarathonResourceType.FS_PARENT
    }
  }

}
