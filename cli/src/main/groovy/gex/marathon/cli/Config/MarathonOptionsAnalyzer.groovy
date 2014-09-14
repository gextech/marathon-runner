package gex.marathon.cli.config

import gex.marathon.core.MarathonUtils
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

    cliBuilder = new CliBuilder(usage: 'marathon', stopAtNonOption: false)
    // Create the list of options.
    cliBuilder.with {
      h  longOpt: 'help', 'show usage information'
      mp longOpt: 'marathon-path', args:1, argName:'path', 'specifies path were modules exist'
      mode longOpt: 'editing-mode', args:1, argName:'mode', 'Edition mode [vi|emacs]'
      config longOpt: 'config-file', args:1, argName:'configFile', 'config file to use. If not specified uses ~/.marathon'
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
      configFile: getOptionValue(options.config)
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
      ]
    ]
  }

  Map parseFromMergingLineAndConfigFile( Map lineOptions, ConfigObject configFileOptions ){
    def optionsMap = getMarathonOptionsMap()

    optionsMap.collectEntries { k, v ->
      String optionValue

      if (isOptionInCommandLine(lineOptions, v.varArgs)) {
        optionValue = getOptionFromCommandLine(lineOptions, v.varArgs)
      } else if (isOptionInConfigFile(configFileOptions, v.varConfigFile)) {
        optionValue = getOptionFromConfigFile(configFileOptions, v.varConfigFile)
      }

      def finalValue

      if (optionValue == null) {
        finalValue = v.default
      } else {
        finalValue = v.converter(optionValue)
        if (finalValue == null) {
          finalValue = v.default
        }
      }
      [k, finalValue]
    }
  }

  List<String> convertToMarathonPath(String value){
    getPathsFromPathVariable(value)
  }

  boolean isOptionInCommandLine(Map options, String optionName){
    options[optionName] && options[optionName] != null
  }

  String getOptionFromCommandLine(Map options, String optionName){
    options[optionName]
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
