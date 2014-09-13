package gex.marathon.cli.Config

import org.jboss.aesh.edit.Mode

/**
 * Created by Tsunllly on 9/12/14.
 */
class OptionsCli {

  OptionAccessor options
  ConfigObject configObject

  OptionsCli( OptionAccessor options, ConfigObject configObject){
    this.options = options
    this.configObject = configObject
  }

  Map parseOptions(){
    def optionsMap = [
      editMode:[
        default: Mode.EMACS,
        varArgs: 'mode',
        varConfigFile: 'editingMode',
        converter:  this.&convertToMode
      ],
      marathonPath : [
        default: defaultMarathonPath(),
        varArgs: 'mp',
        varConfigFile: 'marathonPath',
        converter: this.&convertToMarathonPath
      ]
    ]

    def finalOptions = optionsMap.collectEntries { k, v ->
      String optionValue
      if( isOptionInArgs(v.varArgs) ){
        optionValue = getOptionFromArgs(v.varArgs)
      }else if ( isOptionInConfigFile(v.varConfigFile) ){
        optionValue = getOptionFromConfigFile(v.varConfigFile)
      }

      def finalValue

      if( optionValue == null){
        finalValue = v.default
      }else{
        finalValue = v.converter(optionValue)
        if( finalValue == null){
          finalValue = v.default
        }
      }

      [k, finalValue]
    }

    if(options.arguments().size() == 1) {
      finalOptions.runFile = options.arguments().first()
    }

    finalOptions
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

  List<String> convertToMarathonPath(String value){
    getPathsFromPathVariable(value)
  }

  boolean isOptionInArgs(String optionName){
    options[optionName] && options[optionName] != null
  }

  String getOptionFromArgs(String optionName){
    options[optionName]
  }

  boolean isOptionInConfigFile(String optionName){
    def result = false
    if( configObject ) {
      def v = configObject['settings'][optionName]
      result = (v != null) && (v != [:])
    }
    result
  }

  String getOptionFromConfigFile(String optionName){
    def result = false
    if( configObject ) {
      result = configObject['settings'][optionName]
    }
    result
  }

}
