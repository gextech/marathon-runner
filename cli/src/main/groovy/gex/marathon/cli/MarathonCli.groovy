package gex.marathon.cli

import gex.marathon.cli.Config.MarathonConsole
import gex.marathon.cli.Config.OptionsCli
import gex.marathon.core.MarathonUtils
import gex.marathon.core.MarathonRunner

public class MarathonCli {

  public static String DEFULT_CONFIG_FILEPATH = System.getProperty('user.home')
  public static String DEFULT_CONFIG_FILENAME = '.marathon'

  static OptionAccessor options
  static ConfigObject configObject


  public static void main(String[] args) throws IOException {

    def marathonConsole = new MarathonConsole()
    def cli = buildMarathonCliBuilder()

    options = cli.parse(args)

    if (options.h) {
      cli.usage()
      return
    }

    initializeConfigObject()

    def optionsCli = new OptionsCli(options, configObject)

    Map finalOptions = optionsCli.parseOptions()

    if(finalOptions.runFile) {
      runFile(finalOptions)
    } else {
      // Go to interactive mode
      marathonConsole.init(finalOptions)
    }
  }

  static def runFile(Map finalOptions) {
    MarathonRunner runner = new MarathonRunner(finalOptions.marathonPath)
    File file = new File(finalOptions.runFile)
    String code = file.getText()
    runner.eval(code)
  }


  static def initializeConfigObject( ){
    def configFile

    if( options.config ){
      def file = new File(options.config)
      configFile = file.exists() ? file : configFile
    }
    else{
      configFile = new File( new File(DEFULT_CONFIG_FILEPATH), DEFULT_CONFIG_FILENAME)
    }

    if( configFile ){
      configObject = MarathonUtils.parseConfigFile(configFile)
    }
  }


  public static CliBuilder buildMarathonCliBuilder(){
    def cli = new CliBuilder(usage: 'marathon')
    // Create the list of options.
    cli.with {
      h  longOpt: 'help', 'show usage information'
      mp longOpt: 'marathon-path', args:1, argName:'path', 'specifies path were modules exist'
      mode longOpt: 'editing-mode', args:1, argName:'mode', 'Edition mode [vi|emacs]'
      config longOpt: 'config-file', args:1, argName:'configFile', 'config file to use. If not specified uses ~/.marathon'
    }
    cli
  }

}
