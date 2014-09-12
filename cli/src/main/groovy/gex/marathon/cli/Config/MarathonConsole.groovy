package gex.marathon.cli.Config

import gex.marathon.cli.MarathonConsoleCallBack
import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.Console

/**
 * Created by Tsunllly on 9/11/14.
 */
class MarathonConsole {

  MarathonRunner runner

  Console console

  List<String> marathonPath
  MarathonSettings marathonSettings

  Prompt prompt


  MarathonConsole( ){
    marathonPath = []
    marathonSettings = new MarathonSettings()
    prompt = new Prompt("[marathon>  ] ")
  }


  def init(Map options){
    initMarathonRunner()
    initMarathonConsole(options)
  }

  private initMarathonRunner(){
    runner = new MarathonRunner(marathonPath)
  }

  public initMarathonConsole(Map options){
    marathonSettings.setEditMode(options.editMode)
    console = new Console(marathonSettings.getSettingsBuilder().create());

    console.getShell().out().println(options)
    console.getShell().out().println(getBanner())

    console.setPrompt(prompt);

    console.setConsoleCallback( new MarathonConsoleCallBack(console, runner));
    console.start();
  }

  private String getBanner(){
    MarathonUtils.readResource('/banner')
  }



}
