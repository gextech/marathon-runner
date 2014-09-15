package gex.marathon.cli.config

import gex.marathon.core.MarathonUtils
import org.apache.commons.cli.GnuParser
import org.apache.commons.cli.Option
import org.jboss.aesh.edit.Mode

/**
 * Created by Tsunllly on 9/12/14.
 */
class MarathonOptionsAnalyzer {

  private static final String DEFAULT_CONFIG_FILE_PATH = System.getProperty('user.home')
  private static final String DEFAULT_CONFIG_FILE_NAME = '.marathon'

  CliBuilder cliBuilder

  String[] arguments

  CliBuilder getCliBuilder() {
    return cliBuilder
  }

  MarathonOptionsAnalyzer( String[] arguments){

    cliBuilder = new CliBuilder(usage: 'marathon [options] [targetFile]', header: 'Options:', stopAtNonOption: false, width: 120)
    // Create the list of options.
    cliBuilder.with {
      h  longOpt: 'help', 'Show usage information'
      mp longOpt: 'marathon-path', args:1, argName:'path', 'Specifies marathon path used to require modules'
      mode longOpt: 'editing-mode', args:1, argName:'mode', 'Edition mode [vi|emacs]'
      config longOpt: 'config-file', args:1, argName:'configFile', 'Config file to use. If not specified uses ~/.marathon'
      imp longOpt: 'init-modules-path', args:1, argName:'initModulesPath', 'Path taken for --init modules if path is not specified explicitly'
      im longOpt: 'init-modules', argName: 'modules', args: Option.UNLIMITED_VALUES, valueSeparator: ',' as char, 'List of initial modules to load separated by (,) commas. Example: -im $name:$path,fs:/path/fs,vm:/path/vm,domain. If $path is not specified then --init-modules-path is taken, if not specified then it uses the path corresponding to project resources. Other valid values are [NONE|DEFAULT]'
  }
    cliBuilder

    this.arguments = arguments

  }

  Map parseCommandLine(String[] args = arguments){
    def options = cliBuilder.parse(arguments)
    [
      fileToProcess: getFileToProcess(options),
      help : options.h,
      editMode: options.mode,
      marathonPath: options.mp,
      configFile: getOptionValue(options.config),
      initModules: (options.ims != false) ? options.ims : null,
      initModulesPath: getOptionValue(options.imp)
    ]
  }

  def getOptionValue(def option){
    (option != false) ? option : null
  }


  String getFileToProcess(OptionAccessor options){
    def fileToProcess =  null
    if(options.arguments().size() > 0 &&  options.arguments().first() ==~  /(.*)\w+\.\w+/){
      fileToProcess = options.arguments().first()
    }
    fileToProcess
  }

  ConfigObject parseFromConfigFile(String configFilePath){
    def result
    File configFile

    if( configFilePath ){
      configFile = getFileIfExists( new File(configFilePath) )
    }
    else{
      configFile = getFileIfExists( new File( new File(DEFAULT_CONFIG_FILE_PATH), DEFAULT_CONFIG_FILE_NAME) )
    }

    if( configFile ){
      result = MarathonUtils.parseConfigFile(configFile)
    }
    result
  }

  private File getFileIfExists(File file){
    file.exists() ? file : null
  }


  private Map getMarathonOptionsMap(){
    [
      editMode:[
        default: Mode.EMACS,
        varArgs: 'editMode',
        varConfigFile: 'editingMode',
        converter:  this.&convertToMode
      ],
      marathonPath : [
        default: defaultMarathonPath(),
        varArgs: 'marathonPath',
        varConfigFile: 'marathonPath',
        converter: this.&convertToMarathonPath
      ],
      initModules: [
        default: null,   // No additional actions are required
        varArgs: 'initModules',
        varConfigFile: 'initModules',
        converter: this.&convertToInitModules,
        canBeNull: true
      ],
      initModulesPath: [
        default: null,
        varArgs: 'initModulesPath',
        varConfigFile: 'initModulesPath',
        converter: this.&convertToInitModulesPath,
        canBeNull: true
      ]
    ]
  }

  Map parseFromMergingLineAndConfigFile( Map lineOptions, ConfigObject configFileOptions ){
    def optionsMap = getMarathonOptionsMap()

    def result = optionsMap.collectEntries { k, v ->
      def optionValue

      if (isOptionInCommandLine(lineOptions, v.varArgs)) {
        optionValue = getOptionFromCommandLine(lineOptions, v.varArgs)
      }
      else if (isOptionInConfigFile(configFileOptions, v.varConfigFile)) {
        optionValue = getOptionFromConfigFile(configFileOptions, v.varConfigFile)
      }

      def finalValue
      if (optionValue == null) {
        finalValue = v.default
      } else {
        finalValue =  v.converter(optionValue)
        if ( !v.canBeNull && finalValue == null) {
          finalValue = v.default
        }
      }
      [k, finalValue]
    }
    mergeBetweenOptions(result)
  }

  static Map mergeBetweenOptions( Map options ){
    if(options.initModules == null)
      return options

    String iniModulesPath = options.initModulesPath
    def finalInitModules = options.initModules.each{ module ->
      if(module.path == null){
        module.path = iniModulesPath
      }
    }
    options.initModules = finalInitModules
    options
  }


  List<String> convertToMarathonPath(String value){
    getPathsFromPathVariable(value)
  }

  static List<Map> convertToInitModules(List initModules){

    if(initModules.size() == 1){
      if( initModules.first() == 'NONE' ){
        return []
      }else
      if( initModules.first() == 'DEFAULT' ){
        return null
      }
    }

    List<Map> result = []

    initModules.each { m ->
      def tokens = m.split(':')
      result << [
        name: tokens.first(),
        path: (tokens.size() == 2) ? tokens[1] : null
      ]
    }
   result
  }

  String convertToInitModulesPath(String initModulesPath){
    initModulesPath
  }

  boolean isOptionInCommandLine(Map options, String optionName){
    def result = options[optionName] && options[optionName] != null
    result
  }

  def getOptionFromCommandLine(Map options, String optionName){
    def result = options[optionName]
    result
  }

  boolean isOptionInConfigFile(ConfigObject configObject, String optionName){
    def result = false
    if( configObject ) {
      def v = configObject['settings'][optionName]
      result = (v != null) && (v != [:])
    }
    result
  }

  String getOptionFromConfigFile(ConfigObject configObject, String optionName){
    def result = false
    if( configObject ) {
      result = configObject['settings'][optionName]
    }
    result
  }

  Mode convertToMode(String value){
    EditingMode.parse(value)
  }

  List<String> defaultMarathonPath(){
    List<String> paths = []

    def currentDir = System.getProperty("user.dir");
    paths.add(currentDir)
    paths.add(new File(currentDir, "node_modules").toPath())
    paths.addAll( getPathsFromPathVariable( System.getenv().get("MARATHON_PATH") ))

    paths
  }

  List<String> getPathsFromPathVariable(String pathVariable){
    List<String> paths = []
    pathVariable?.split(":").each{
      paths.add(it)
    }
    paths
  }

}
