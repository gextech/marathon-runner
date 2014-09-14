package gex.marathon.cli

import gex.marathon.core.MarathonRunner
import gex.marathon.core.MarathonUtils
import org.jboss.aesh.console.Console
import org.jboss.aesh.console.Prompt
import org.jboss.aesh.console.settings.SettingsBuilder

/**
 * Created by Tsunllly on 9/11/14.
 */
class MarathonFileProcessor {

  MarathonRunner runner
  Map options

  MarathonFileProcessor(Map options){
    this.options = options
    this.runner = new MarathonRunner(options.marathonPath, options.initModules)
  }


  Object processFile(String filePath){
    runner.eval(new File(filePath).text)
  }

}
