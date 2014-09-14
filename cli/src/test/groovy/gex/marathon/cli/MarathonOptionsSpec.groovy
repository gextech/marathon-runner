package gex.marathon.cli

import gex.marathon.cli.config.MarathonOptionsAnalyzer
import spock.lang.*

class MarathonOptionsSpec extends Specification {

  def "Test filePath as argument works with other options"() {
    given:
      String[] arguments = ['-h', '-mp', '/marathon/path', 'file.js', ]
      MarathonOptionsAnalyzer optionsCli = new MarathonOptionsAnalyzer(arguments)

    when:
      def map = optionsCli.parseCommandLine(arguments)

    then:
      map.help == true
      map.marathonPath == '/marathon/path'
      map.fileToProcess == 'file.js'
      map.configFile == null
  }


  def "Test command line with a list with multiple defaults "() {
    given:
      String[] arguments = ['-d', 'fs:/path/to/fs;domain:/path/to/domain;vm', '-config', '/config/file' ]
      MarathonOptionsAnalyzer optionsCli = new MarathonOptionsAnalyzer(arguments)

    when:
      def map = optionsCli.parseCommandLine(arguments)

    then:
      map.defaults.size() == 3
      map.defaults == ['fs:/path/to/fs', 'domain:/path/to/domain', 'vm']
      map.configFile == '/config/file'
  }

  
}
