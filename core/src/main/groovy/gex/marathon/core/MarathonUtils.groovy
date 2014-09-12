package gex.marathon.core

class MarathonUtils {

  static String readResource(String path) {
    new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path))).getText()
  }

  static ConfigObject parseConfigFile(File file) {
    def config
    if(file.exists() ) {
      config = new ConfigSlurper().parse(file.toURI().toURL())
    }
    config
  }

  static ConfigObject parseConfigFile(String filename) {
    def config
    if(new File(filename).exists() ) {
      config = new ConfigSlurper().parse(new File(filename).toURI().toURL())
    }
    config
  }

}

