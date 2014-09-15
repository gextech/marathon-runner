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

  def "Test command line with a list with multiple initModules "() {
    given:
      String[] arguments = ['-im', 'fs:/path/to/fs,domain:/path/to/domain,vm', '-config', '/config/file' ]
      MarathonOptionsAnalyzer optionsCli = new MarathonOptionsAnalyzer(arguments)

    when:
      def map = optionsCli.parseCommandLine(arguments)

    then:
      map.initModules.size() == 3
      map.configFile == '/config/file'
  }

  def "Test command line with value NONE|DEFAULT of initModules"() {
    given:
      String[] arguments = ['-im', initModuleValue, '-config', '/config/file' ]
      MarathonOptionsAnalyzer optionsCli = new MarathonOptionsAnalyzer(arguments)

    when:
      def map = optionsCli.parseCommandLine(arguments)

    then:
      map.initModules.size() == expected
      map.configFile == '/config/file'

    where:
      initModuleValue || expected
      'NONE'          || 1
      'DEFAULT'       || 1
  }


  def "Test mergeBetweenOptions: initModules and initModulesPath"() {
    given:
      def initModules = [
        [name: 'fs', path: '/path/to/fs'],
        [name: 'domain', path: '/path/to/domain'],
        [name: 'vm', path: null],
      ]

      def options = [
        initModules    : initModules,
        initModulesPath: initModulesPath
      ]

    when:
      def result = MarathonOptionsAnalyzer.mergeBetweenOptions(options)

    then:
      result.initModules == expected

    where:
      initModulesPath     || expected

      null                ||  [
        [name: 'fs', path: '/path/to/fs'],
        [name: 'domain', path: '/path/to/domain'],
        [name: 'vm', path: null],
      ]

      'init/modules/path' || [
        [name: 'fs', path: '/path/to/fs'],
        [name: 'domain', path: '/path/to/domain'],
        [name: 'vm', path: 'init/modules/path'],
      ]
  }


  def "Test convertToInitModules"() {
    given:
      def initModules = ['fs:/path/to/fs', 'domain:/path/to/domain', 'vm']

    when:
      def result = MarathonOptionsAnalyzer.convertToInitModules(initModules)

    then:
      result.size() == 3
      result == [
        [name:'fs', path: '/path/to/fs'],
        [name:'domain', path: '/path/to/domain'],
        [name:'vm', path: null],
      ]

    when:
      initModules = ['fs:Documents/jsmodules']
      result = MarathonOptionsAnalyzer.convertToInitModules(initModules)

    then:
      result.size() == 1
      result == [
        [name:'fs', path: 'Documents/jsmodules']
      ]
  }


}
