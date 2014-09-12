package gex.marathon.cli.Config

import org.jboss.aesh.console.settings.SettingsBuilder
import org.jboss.aesh.edit.Mode

/**
 * Created by Tsunllly on 9/11/14.
 */
class MarathonSettings {

  private SettingsBuilder settingsBuilder

  MarathonSettings(){
    settingsBuilder = new SettingsBuilder()
  }

  SettingsBuilder getSettingsBuilder() {
    return settingsBuilder
  }

  public SettingsBuilder setEditMode( Mode mode ){
    settingsBuilder.mode(mode)
  }


}
