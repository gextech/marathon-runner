package gex.marathon.path

import groovy.transform.CompileStatic

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.util.jar.Attributes
import java.util.jar.JarFile

@CompileStatic
class MarathonPathReader {
  private static final Attributes.Name PACKAGE_NAME = new Attributes.Name("Npm-Name")

  private List globalPaths
  private List relativePaths
  private Map jars
  private MarathonPathResource parentResource

  private List addedExtensions = ["", ".js", ".json"]
  private List defaultLoadingPaths = ["node_modules"]

  MarathonPathReader(MarathonPathResource parentResource = null) {
    globalPaths = new ArrayList<Path>()
    relativePaths = new ArrayList<Path>()
    jars = new HashMap()

    if(parentResource) {
      setupParentResource(parentResource)
    }

  }

  public void addExtensions(Set extensions) {
    Set currentExtensions = addedExtensions.toSet()
    currentExtensions.addAll(extensions)
    addedExtensions = currentExtensions.toList()
  }

  private void setupParentResource(MarathonPathResource parent) {
    parentResource = parent
    if(parent.originPath.fileSystem == FileSystems.default) {
      for(String path in defaultLoadingPaths) {
        path = parent.path.parent.toString() + File.separator + path
        addPathToList(path, relativePaths)
      }
    } else {
      def attrs = parent.originAttributes
      def prependPath = attrs.get("prependPath")
      jars.put(parent.originPath.fileSystem, attrs)
      if(prependPath == "true") {
        for(String path in defaultLoadingPaths) {
          def pathFile = parent.path.fileSystem.getPath(path)
          relativePaths.add(pathFile)
        }
      }
    }
  }

  void addPath(String path) {
    addPathToList(path, globalPaths)
  }

  void clearPath() {
    globalPaths.clear()
    jars.clear()
  }

  private void addPathToList(String path, List paths) {
    def p = FileSystems.getDefault().getPath(path)
    if(p.toFile().exists()) {
      if(p.toFile().isDirectory()) {
        paths.add(p)
      } else {
        JarFile j = new JarFile(p.toFile())
        def attrs = new HashMap(j.manifest.mainAttributes)
        attrs.put("prependPath", "true")
        def fs = FileSystems.newFileSystem(p, null)
        p = fs.getPath(fs.separator)
        jars.put(fs, attrs)
        paths.add(p)
      }
    }
  }

  public MarathonPathResource resolvePath(String path) {
    MarathonPathResource result
    if(path.startsWith("./") && parentResource) {
      def fs = parentResource.originPath.fileSystem
      Path rootPath
      rootPath = parentResource.path
      if(Files.isRegularFile(rootPath)) {
        rootPath = rootPath.parent
      }
      result = resolvePathInPaths(path, [rootPath])
    } else {
      result = resolvePathInPaths(path, relativePaths)
      if(!result) {
        result = resolvePathInPaths(path, globalPaths)
      }
    }
    result
  }
  
  private MarathonPathResource resolvePathInPaths(String path, List<Path> paths) {
    Path pathFile
    for(Path p in paths) {
      String lookupPath = path

      Map attrs = (Map)jars.get(p.fileSystem)
      if(attrs) {
        if(attrs.get(PACKAGE_NAME)) {
          def name = attrs.get(PACKAGE_NAME).toString()
          if(path.startsWith(name + File.separator)) {
            lookupPath = path.replace(name + File.separator, "")
          } else if(path == name) {
            lookupPath = path.replace(name, "")
          }
        }
      }

      pathFile = resolveSubdirPath(p, lookupPath)
      if(pathFile) {
        return new MarathonPathResource(path: pathFile, originPath: p, originAttributes: attrs)
      }
    }
    null
  }

  private Path resolveFileSystemPath(FileSystem f, String path) {
    for(it in addedExtensions) {
      def result = f.getPath(path + it)
      if(Files.exists(result)) {
        return result
      }
    }
    null
  }

  private Path resolveSubdirPath(Path p, String path) {
    for(it in addedExtensions) {
      def result = p.resolve(path + it)
      if(Files.exists(result)) {
        def relativePath = p.toAbsolutePath().relativize(result.toAbsolutePath())
        if(relativePath.toString().contains("..")) {
          throw new SecurityException("You are trying to access a resource ${path} outside marathon path ${p}")
        }
        return result
      }
    }
    null
  }

  MarathonPathResource getParentResource() {
    parentResource
  }

  List getGlobalPaths() {
    globalPaths
  }

}

