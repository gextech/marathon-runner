package gex.marathon.cli

import gex.marathon.core.MarathonContext
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

  MarathonConsoleCallBack(Console console, MarathonRunner runner){
    this.console = console
    this.runner = runner
  }

  @Override
  public int execute(ConsoleOperation output) {
    def input = output.getBuffer()

    if (input.equals("quit")) {
      quitConsole()
    }else{
      evaluateExpression(input)
    }

    return 0;
  }


  private quitConsole(){
    try {
      console.stop();
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  private evaluateExpression(String userInput){

    def writer = new PrintWriter(console.getShell().out())
    def errorWriter = new PrintWriter(console.getShell().err())

    runner.context.setWriter(writer)
    runner.context.setErrorWriter(errorWriter)

    def retValue = runner.eval(userInput)
    runner.invokeMethod("console", "log", retValue)

    writer.flush()
    errorWriter.flush()
  }



  @Override
  CommandOperation getInput() throws InterruptedException {
  }

  @Override
  void setProcess(org.jboss.aesh.console.Process process) {
  }

}

