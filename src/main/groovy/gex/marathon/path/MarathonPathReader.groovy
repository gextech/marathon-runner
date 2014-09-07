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
  private static final String PACKAGE_NAME = "Npm-Name"

  private List paths
  private Map jars

  List defaultExtensions = ["", ".js"]

  MarathonPathReader() {
    paths = new ArrayList<Path>()
    jars = new HashMap()
  }

  public void addPath(String path) {
    def p = FileSystems.getDefault().getPath(path)
    if(p.toFile().exists()) {
      if(p.toFile().isDirectory()) {
        paths.add(p)
      } else {
        JarFile j = new JarFile(p.toFile())
        def attrs = j.manifest.mainAttributes
        def fs = FileSystems.newFileSystem(p, null)
        jars.put(fs, attrs)
        paths.add(fs)
      }
    }
  }

  public boolean fileExists(String path) {
    Path pathFile
    for(p in paths) {
      if(p instanceof FileSystem) {
        FileSystem fs = (FileSystem)p
        Attributes attrs = (Attributes)jars.get(fs)
        def name = attrs.getValue(PACKAGE_NAME) + "/"
        if(path.startsWith(name)) {
          pathFile = resolveFileSystemPath(fs, path.replace(name, ""))
        }
        if(!pathFile) {
          pathFile = resolveFileSystemPath(fs, path)
        }
      } else {
        pathFile = resolveSubdirPath((Path)p, path)
      }
      if(pathFile) {
        def attrs = Files.readAttributes(pathFile, BasicFileAttributes.class)
        return attrs.isRegularFile()
      }
    }
    false
  }

  public boolean pathExists(String path) {
    def pathFile
    for(p in paths) {
      if(p instanceof FileSystem) {
        FileSystem fs = (FileSystem)p
        Attributes attrs = (Attributes)jars.get(fs)
        def name = attrs.getValue(PACKAGE_NAME) + "/"
        if(path.startsWith(name)) {
          pathFile = resolveFileSystemPath(fs, path.replace(name, ""))
        }
        if(!pathFile) {
          pathFile = resolveFileSystemPath(fs, path)
        }
      } else {
        pathFile = resolveSubdirPath((Path)p, path)
      }
      if(pathFile) return true
    }
    false
  }

  public Path resolvePath(String path) {
    def pathFile
    for(p in paths) {
      if(p instanceof FileSystem) {
        FileSystem fs = (FileSystem)p
        Attributes attrs = (Attributes)jars.get(fs)
        def name = attrs.getValue(PACKAGE_NAME) + "/"
        if(path.startsWith(name)) {
          pathFile = resolveFileSystemPath(fs, path.replace(name, ""))
        }
        if(!pathFile) {
          pathFile = resolveFileSystemPath(fs, path)
        }
      } else {
        pathFile = resolveSubdirPath((Path)p, path)
      }
      if(pathFile) return pathFile
    }
    throw new IllegalArgumentException("Path ${path} not found")
  }

  private Path resolveFileSystemPath(FileSystem f, String path) {
    for(it in defaultExtensions) {
      def result = f.getPath(path + it)
      if(Files.exists(result)) {
        return result
      }
    }
    null
  }

  private Path resolveSubdirPath(Path p, String path) {
    for(it in defaultExtensions) {
      def result = p.resolve(path + it)
      if(Files.exists(result)) {
        def relativePath = p.relativize(result)
        if(relativePath.toString().contains("..")) {
          throw new SecurityException("You are trying to access a resource ${path} outside marathon path ${p}")
        }
        return result
      }
    }
    null
  }

  List getPaths() {
    paths
  }

}

