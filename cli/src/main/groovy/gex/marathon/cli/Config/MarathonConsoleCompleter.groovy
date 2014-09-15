package gex.marathon.cli.config

import org.jboss.aesh.complete.CompleteOperation
import org.jboss.aesh.complete.Completion

/**
 * Created by tsunllly on 9/15/14.
 */
class MarathonConsoleCompleter implements Completion{

  @Override
  void complete(CompleteOperation co) {
    // very simple completor

    def completionCandidate = getCompletionCandidate(co.getBuffer())
    if(completionCandidate){
      co.addCompletionCandidate(completionCandidate)
    }
  }

  public String getCompletionCandidate(String input){
    def candidate = null
    def map = [
      [partial: ':quit', final:':quit'],
      [partial: ':prompt', final:':prompt > '],
      [partial: ':reload', final:':reload false'],
      [partial: ':clear', final:':clear'],
      [partial: ':settings', final:':settings'],
    ]

    if(input.size() >= 2) {
      map.each {
        if(it.partial.contains(input)) {
          candidate = it.final
          return
        }
      }
    }

    candidate
  }


}
