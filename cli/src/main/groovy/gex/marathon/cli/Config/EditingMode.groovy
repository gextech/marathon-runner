package gex.marathon.cli.Config

import org.jboss.aesh.edit.Mode

/**
 * Created by Tsunllly on 9/11/14.
 */
public class EditingMode {

  static def mapValues = [
    [k: Mode.VI,    v: ['vi']],
    [k: Mode.EMACS, v: ['emacs']]
  ]


  public static Mode parse(String s){
    def r = mapValues.find {
      s in it.v
    }?.k

    r ?: Mode.EMACS
  }


}