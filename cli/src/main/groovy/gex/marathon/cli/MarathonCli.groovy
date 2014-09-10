package gex.marathon.cli

import org.jboss.aesh.console.Console;

import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.SettingsBuilder;

public class MarathonCli {

  public static void main(String[] args) throws IOException {

    final Console console = new Console(new SettingsBuilder().create());

    console.setPrompt(new Prompt("[marathon>   ] "));
    console.setConsoleCallback( new MarathonConsoleCallBack(console));
    console.start();
  }
}