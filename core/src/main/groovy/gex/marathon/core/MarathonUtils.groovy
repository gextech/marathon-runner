package gex.marathon.core

class MarathonUtils {

  static String readResource(String path) {
    new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path))).getText()
  }

}

