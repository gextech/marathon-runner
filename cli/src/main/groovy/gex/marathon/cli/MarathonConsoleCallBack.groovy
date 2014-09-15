package gex.marathon.cli

import gex.marathon.core.MarathonContext
import gex.marathon.core.MarathonRunner
import org.jboss.aesh.console.ConsoleCallback
import org.jboss.aesh.console.ConsoleOperation
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.command.CommandOperation
import org.jboss.aesh.console.Console;

/**
 * Created by Tsunllly on 9/10/14.
 */
class MarathonConsoleCallBack implements ConsoleCallback {

  Console console
  MarathonRunner runner
  PrintWriter writer
  PrintWriter errorWriter

  MarathonConsoleCallBack(Console console, MarathonRunner runner){
    this.console = console
    this.runner = runner

    writer = new PrintWriter(console.getShell().out())
    errorWriter = new PrintWriter(console.getShell().err())

    runner.context.setWriter(writer)
    runner.context.setErrorWriter(errorWriter)
  }

  def quit  = {
    try {
      console.stop();
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  def prompt(def m){
    console.setPrompt( new Prompt("[${m[0][1]}] "))
  }

  @Override
  public int execute(ConsoleOperation output) {
    def input = output.getBuffer().trim()

    def commands = [
      [regex: "quit|exit|:quit|:exit|:Q|:q", method: quit ],
      [regex: ":set prompt (.*)", method: this.&prompt ],
    ]

    def f = commands.find{
      def match = input  =~ it.regex
      match.count > 0
    }

    if( f ) {
      f.method( input =~ f.regex )
    } else {
      evaluateExpression(input)
    }
    return 0;
  }



  private evaluateExpression(String userInput){
    try {
      def retValue = runner.eval(userInput)
      runner.invokeMethod("console", "log", retValue)
    }
    catch (Exception e){
      e.printStackTrace(errorWriter)
      runner.invokeMethod("console", "log", error)
    }
    finally {
      writer.flush()
      errorWriter.flush()
    }
  }

  @Override
  CommandOperation getInput() throws InterruptedException {
  }

  @Override
  void setProcess(org.jboss.aesh.console.Process process) {
  }

  void setWriter(PrintWriter writer) {
    this.writer = writer
  }

  void setErrorWriter(PrintWriter errorWriter) {
    this.errorWriter = errorWriter
  }


}

