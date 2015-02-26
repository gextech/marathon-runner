Marathon Runner
===============

Master: [![Build Status](https://travis-ci.org/gextech/marathon-runner.png)](https://travis-ci.org/gextech/marathon-runner)

The marathon runner is a very simple (and opinionated) nashorn runner that aims to be node friendly.

Our main goal is to have a battle tested production environment, while
enjoying the benefits of using a better language and development environment.

Just to be clear this is NOT a complete node replacement, instead we want to be able to use npm (or bower)
dependencies that don't depend a lot on the node runtime.

Now let's list our specific goals:

* To be able to use CommonJS
* To mimic NodeJS dependency resolution
* To make reactive extensions interoperable with java
* Just enough node to run coffee


### Marathon CLI

**Usage:** marathon [options] [targetFile]

**Options:**

`-h,--help`
 Show usage information

 `-config,--config-file <configFile>`
 Config file to use. If not specified uses ~/.marathon. Command line options has priority over config file ones.

 *Example:* *-config /home/marathon_file.config*

 `-mp,--marathon-path <path>`
Specifies marathon path used to require modules. If not specified then current directory path is taken, and [current directory]/node_modules and environment variable $MARATHON_PATH

 *Example:* *-mp /one/path:/another/path*


 `-im,--init-modules <modules>`
 List of initial modules to load separated by (,) commas.

 *Example:* *-im                                       $name:$path,fs:/path/fs,vm:/path/vm,domain*

 - If $path is not specified then *--init-modules-path* is taken.
 - If *--init-modules-path* not specified then it uses the path corresponding to project resources.
 - Other valid values are *[NONE|DEFAULT]*

`-imp,--init-modules-path <initModulesPath>`
Path taken in *--init-modules*  when path is not specified explicitly

*Example:* *-imp /path/to/init/modes*

`-mode,--editing-mode <mode>`
Edition mode [vi|emacs]

*Example:* *-mode vi*

`-r,--reload-context`
If present, each evaluation is made with a different context


**Example of config file (~/.marathon):**

 Command line options has priority over config file ones.

```c
settings {

  // Mode vi or emacs
  editMode = 'vi'

  // Path for modules to require
  marathonPath = '/path/one/node_modules:/path/two/node_modules'

  // Map with initial modules to load and their respective paths
  initModules = 'fs,mv,domain:/path/domain'

  // Default path for initial modules if not specified in initModules path
  initModulesPath = '/init/modules/path'

}

```

##### Prompt commands

`quit|exit|:quit|:exit|:q`
To close cli

`:prompt <attempted promp>`
It sets the prompt symbol. Example `:prompt >>` set the prompt symbol to `>>`

`:reload`
To ask for th ecurrent reload mode

`:reload true`, `:reload false`
Changes the reload mode. If true the context is new for each evaluation

`:clear`
Erase the screen

`:settings`
To ask for the current settings
* **editMode** EMACS or VI
* **marathonPath** Specifies marathon path used to require modules
* **initModules** Initial modules which were loaded _[moduleName,pathWereToFindIt]_
* **initModulesPath** Path were initial modules are taken if **_initModules_** was not set

