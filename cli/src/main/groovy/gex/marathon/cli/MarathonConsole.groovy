package gex.marathon.cli

import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.Console
import org.jboss.aesh.console.settings.SettingsBuilder

/**
 * Created by Tsunllly on 9/11/14.
 */
class MarathonConsole {

  MarathonRunner runner
  Map options

  Console console
  Prompt prompt


  MarathonConsole( Map options ){
    prompt = new Prompt("[marathon>  ] ")
    this.options = options
    runner = new MarathonRunner(options.marathonPath, options.initModules)
  }

  public init(){
    SettingsBuilder settingsBuilder = new SettingsBuilder()
    settingsBuilder.parseOperators(false)
    settingsBuilder.mode(options.mode)

    console = new Console(settingsBuilder.create());

    console.getShell().out().println(getBanner())

    console.setPrompt(prompt);

    console.setConsoleCallback( new MarathonConsoleCallBack(console, runner) );
    console.start();
  }

  private String getBanner(){
    MarathonUtils.readResource('/banner')
  }

}
