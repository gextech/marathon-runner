package gex.marathon.cli.Config

import gex.marathon.cli.MarathonConsoleCallBack
import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.Console
import org.jboss.aesh.console.settings.SettingsBuilder
import org.jboss.aesh.edit.Mode

/**
 * Created by Tsunllly on 9/11/14.
 */
class MarathonConsole {
  Console console
  Map options
  Prompt prompt


  MarathonConsole( ){
    prompt = new Prompt("[marathon>  ] ")
  }

  def init(Map options){
    this.options = options
    MarathonRunner runner = new MarathonRunner(options.marathonPath)
    initMarathonConsole(runner, options.editMode)
  }


  public initMarathonConsole(MarathonRunner runner, Mode editMode){
    SettingsBuilder settingsBuilder = new SettingsBuilder()
    settingsBuilder.parseOperators(false)
    settingsBuilder.mode(editMode)

    console = new Console(settingsBuilder.create());

    console.getShell().out().println(options)
    console.getShell().out().println(getBanner())

    console.setPrompt(prompt);

    console.setConsoleCallback( new MarathonConsoleCallBack(console, runner) );
    console.start();
  }

  private String getBanner(){
    MarathonUtils.readResource('/banner')
  }

}
