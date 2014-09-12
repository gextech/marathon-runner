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
        default: ':)',
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

      def finalValue = v.converter(optionValue)

      if( finalValue == null){
        finalValue = v.default
      }
      [k, finalValue]
    }

    finalOptions
  }


  Mode convertToMode(String value){
    EditingMode.parse(value)
  }

  String convertToMarathonPath(String value){
    value
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
      result = configObject['settings'][optionName] != null
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
