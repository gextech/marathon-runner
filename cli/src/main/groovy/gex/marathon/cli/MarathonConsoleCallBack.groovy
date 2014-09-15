package gex.marathon.cli

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

  Boolean reloadContext

  MarathonConsoleCallBack(Console console, MarathonRunner runner, Boolean reloadContext = false){
    this.console = console
    this.runner = runner
    this.reloadContext = reloadContext

    if( console && runner ) {
      writer = new PrintWriter(console.getShell().out())
      errorWriter = new PrintWriter(console.getShell().err())

      runner.context.setWriter(writer)
      runner.context.setErrorWriter(errorWriter)
    }
  }

  @Override
  public int execute(ConsoleOperation output) {
    def input = output.getBuffer().trim()

    def f = getMatchWithCommand(input)

    if( f ) {
      f.method( input =~ f.regex )
    } else {
      evaluateExpression(input)
    }

    return 0;
  }

  public def getMatchWithCommand(String input){

    def commands = [
      [name: 'quit', regex: "quit|exit|:quit|:exit|:q", method: this.&quit ],
      [name: 'prompt', regex: ":set prompt (.*)", method: this.&prompt ],
      [name: 'reload', regex: ":set reload (true|false)", method: this.&reload ],
      [name: 'getReload', regex: ":get reload", method: this.&getReload ],
      [name: 'clear', regex: ":clear|clear", method: this.&clear ],
      [name: 'settings', regex: ":get settings", method: this.&showSettings ]
    ]

    def f = commands.find{
      def match = input.toLowerCase()  =~ it.regex
      match.count > 0
    }

    f
  }

  def quit(def m){
    try {
      console.stop();
    } catch (IOException e) {
      e.printStackTrace()
    }
  }

  def prompt(def m){
    console.setPrompt( new Prompt("[${m[0][1]}] "))
  }

  def reload(def m){
    this.reloadContext = Boolean.parseBoolean(m[0][1])
    printReloadContextStatus()
  }

  def getReload(def m){
    printReloadContextStatus()
  }

  def printReloadContextStatus(){
    writer.println("Reloading context on each evaluation: $reloadContext")
    writer.flush()
  }

  def clear(def m){
    console.clear()
  }

  def showSettings(def m){
    runner.options.each { k, v ->
      writer.println("$k : $v")
    }
    writer.flush()
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
      if(reloadContext){
        runner = new MarathonRunner(runner.options)
      }
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

