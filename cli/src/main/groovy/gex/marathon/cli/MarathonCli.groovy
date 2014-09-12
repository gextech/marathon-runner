package gex.marathon.cli

import gex.marathon.cli.Config.EditingMode
import gex.marathon.cli.Config.MarathonConsole
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.edit.Mode;

public class MarathonCli {


  static Mode DEFAULT_MODE = Mode.EMACS
  private static final String DEFULT_CONFIG_FILEPATH = System.getProperty('user.home')
  private static final String DEFULT_CONFIG_FILENAME = '.marathon'

  static OptionAccessor options
  static File configFile
  static ConfigObject configObject


  public static void main(String[] args) throws IOException {

    configFile = new File( new File(DEFULT_CONFIG_FILEPATH), DEFULT_CONFIG_FILENAME)
    configObject = MarathonUtils.parseConfigFile(configFile)

    def cli = buildMarathonCliBuilder()
    def marathonConsole = new MarathonConsole()

    options = cli.parse(args)

    if (options.h) {
      cli.usage()
      return
    }

    if( options.config ){
      def file = new File(options.config)
      configFile = file.exists() ? file : configFile
      configObject = MarathonUtils.parseConfigFile(configFile)
    }

    Map finalOptions = parseOptions()
    marathonConsole.init(finalOptions)
  }

  public static CliBuilder buildMarathonCliBuilder(){
    def cli = new CliBuilder(usage: 'marathon')
    // Create the list of options.
    cli.with {
      h  longOpt: 'help', 'show usage information'
      mp longOpt: 'marathonpath', args:1, argName:'path', 'specifies path were modules exist'
      mode longOpt: 'editing-mode', args:1, argName:'mode', 'Edition mode [vi|emacs]'
      config longOpt: 'config-file', args:1, argName:'configFile', 'config file to use. If not specified uses ~/.marathon'
    }
    cli
  }


  static Map parseOptions(){
    [
      editMode     : getEditMode(),
      marathonPath : getMarathonPath()
    ]
  }


  private static Mode getEditMode(){
    Mode mode = DEFAULT_MODE
    if( isModeInArgs() ){
      mode = getModeFromArgs()
    }else if( isModeInConfigFile() ){
      mode = getEditModeFromConfigFile()
    }
    mode
  }


  static boolean isModeInArgs(){
    options.mode && options.mode != null
  }

  static Mode getModeFromArgs(){
    EditingMode.parse(options.mode) ?: Mode.EMACS
  }

  static boolean isModeInConfigFile(){
    configObject?.settings?.editingMode != null
  }

  static private Mode getEditModeFromConfigFile(){
    EditingMode.parse(configObject.settings.editingMode)
  }


  private static List<String> getMarathonPath(){
    List<String> marathonPath = []
    if( isMarathonPathInArgs() ){
      marathonPath = getMarathonPathFromArgs()
    }else if( isMarathonPathInConfigFile() ){
      marathonPath = getMarathonFromConfigFile()
    }
    marathonPath
  }

  static boolean isMarathonPathInArgs(){
    options.mp && options.mp != null
  }

  private static List<String> getMarathonPathFromArgs(){
    List<String> marathonPath = []
    marathonPath.add(options.mp)
    marathonPath
  }

  static boolean isMarathonPathInConfigFile(){
    configObject?.settings?.marathonPath != null
  }

  static private List<String> getMarathonFromConfigFile(){
    List<String> marathonPath = []
    marathonPath.add(configObject.settings.marathonPath)
    marathonPath
  }




}