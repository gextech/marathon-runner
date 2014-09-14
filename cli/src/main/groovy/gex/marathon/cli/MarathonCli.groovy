package gex.marathon.cli

import gex.marathon.cli.Config.MarathonOptionsAnalyzer

public class MarathonCli {

  public static void main(String[] args) throws IOException {
    def marathonOptionsAnalyzer = new MarathonOptionsAnalyzer(args)

    def lineOptions = marathonOptionsAnalyzer.parseCommandLine()

    if (lineOptions.help) {
      marathonOptionsAnalyzer.getCliBuilder().usage()
      return
    }

    def configFileOptions = marathonOptionsAnalyzer.parseFromConfigFile(lineOptions.configFile)

    def marathonOptions = marathonOptionsAnalyzer.parseFromMergingLineAndConfigFile(lineOptions, configFileOptions)

    if( lineOptions.fileToProcess ){
      def marathonFileProcessor = new MarathonFileProcessor(marathonOptions)
      marathonFileProcessor.processFile(lineOptions.fileToProcess)
    }
    else
    {
      def marathonConsole = new MarathonConsole(marathonOptions)
      marathonConsole.init()
    }
  }

}