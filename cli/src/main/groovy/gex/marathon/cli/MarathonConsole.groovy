package gex.marathon.cli

import gex.marathon.cli.config.MarathonConsoleCompleter
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

  Boolean reloadContext

  MarathonConsole( Map options, Boolean reloadContext = false){
    prompt = new Prompt("[marathon>  ] ")
    this.options = options
    runner = new MarathonRunner(options)
    this.reloadContext = reloadContext
  }

  public init(){
    SettingsBuilder settingsBuilder = new SettingsBuilder()
    settingsBuilder.parseOperators(false)
    settingsBuilder.mode(options.editMode )

    console = new Console(settingsBuilder.create());

    console.addCompletion(new MarathonConsoleCompleter());

    console.getShell().out().println(getBanner())

    console.setPrompt(prompt);

    console.setConsoleCallback( new MarathonConsoleCallBack(console, runner, reloadContext) );
    console.start();
  }

  private String getBanner(){
    this.getClass().getResourceAsStream('/banner').text
  }

}
