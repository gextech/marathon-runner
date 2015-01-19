package gex.marathon.core;

public class ResourceLoader {

  private ClassLoader classLoader

  ResourceLoader(){
    classLoader = getDefaultClassLoader()
  }

  ResourceLoader(ClassLoader cl){
    this.classLoader = cl
  }

  public static ClassLoader getDefaultClassLoader() {
    ClassLoader cl = null
    try {
      cl = Thread.currentThread().getContextClassLoader();
    }
    catch (Throwable ex) {
      // Cannot access thread context ClassLoader - falling back to system class loader...
    }
    if (cl == null) {
      // No thread context class loader -> use class loader of this class.
      cl = ResourceLoader.class.getClassLoader()
    }
    return cl
  }

  public InputStream getInputStream(String path) throws IOException {
    if (path.startsWith("/")) {
      path = path.substring(1)
    }
    InputStream is = classLoader.getResourceAsStream(path)
    if (is == null) {
      throw new FileNotFoundException("$path cannot be opened because it does not exist")
    }
    return is
  }

}
