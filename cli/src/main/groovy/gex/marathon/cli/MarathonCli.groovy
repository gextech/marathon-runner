package gex.marathon.cli

import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.settings.SettingsBuilder
import org.jboss.aesh.console.Console;

public class MarathonCli {

  static MarathonRunner runner

  public static void main(String[] args) throws IOException {

    def cli = buildMarathonCliBuilder()

    def options = cli.parse(args)

    if (options.h) {
      cli.usage()
      return
    }

    if(options.f){
      this.processFile()
      return
    }

    processConsole(options)
  }

  private static processConsole(def options){
    def marathonPath = getMarathonPathFromOptions(options)
    initMarathonRunner(marathonPath)
    initMarathonConsole()
  }



  private static getMarathonPathFromOptions(def options){
    List<String> marathonPath = []
    if(options.mp){
      marathonPath.add(options.mp)
    }
    marathonPath
  }


  public static CliBuilder buildMarathonCliBuilder(){
    def cli = new CliBuilder(usage: 'marathon')
    // Create the list of options.
    cli.with {
      h  longOpt: 'help', 'show usage information'
      f  longOpt: 'file', args:1, argName:'file', 'executes file'
      mp longOpt: 'marathonpath', args:1, argName:'path', 'specifies path were modules exist'
    }
    cli
  }

  public static initMarathonConsole(){
    final Console console = new Console(new SettingsBuilder().create());
    console.getShell().out().println(getBanner())
    console.setPrompt(new Prompt("[marathon>   ] "));
    console.setConsoleCallback( new MarathonConsoleCallBack(console, runner));
    console.start();
  }

  private static initMarathonRunner(List<String> marathonPath = []){
    this.runner = new MarathonRunner(marathonPath)
  }

  private static String getBanner(){
    MarathonUtils.readResource('/banner')
  }

  private static processFile(){
    println("TODO: Process file")
  }



}