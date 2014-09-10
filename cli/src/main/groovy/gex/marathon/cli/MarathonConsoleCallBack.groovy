package gex.marathon.cli

import gex.marathon.core.MarathonRunner
import org.jboss.aesh.console.ConsoleCallback
import org.jboss.aesh.console.ConsoleOperation
import org.jboss.aesh.console.command.CommandOperation
import org.jboss.aesh.console.Console;

/**
 * Created by Tsunllly on 9/10/14.
 */
class MarathonConsoleCallBack implements ConsoleCallback {
  Console console
  MarathonRunner runner


  MarathonConsoleCallBack(Console console){
    this.runner = new MarathonRunner()
    this.console = console
  }

  @Override
  public int execute(ConsoleOperation output) {
    def input = output.getBuffer()



    def retValue = runner.eval(input)

    console.getShell().out().println("======> $retValue");

    if (output.getBuffer().equals("quit")) {
      try {
        console.stop();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return 0;
  }

  @Override
  CommandOperation getInput() throws InterruptedException {
  }

  @Override
  void setProcess(org.jboss.aesh.console.Process process) {
  }

}

