package gex.marathon.module

import gex.marathon.core.MarathonContext
import gex.marathon.core.MarathonCoreEngine
import gex.marathon.core.MarathonUtils
import gex.marathon.core.ResourceLoader
import gex.marathon.path.MarathonPathReader
import gex.marathon.path.MarathonPathResource

import groovy.transform.CompileStatic
import groovy.json.JsonSlurper

import java.util.jar.Manifest

@CompileStatic
class MarathonModuleLoader {
  private Map<String, MarathonModule> moduleCache
  private MarathonPathResource resource
  private MarathonPathReader reader
  private MarathonCoreEngine engine
  private ResourceLoader resourceLoader

  Map<String, Object> extensionLoaders

  private final static String DEFAULT_MODULES_PATH = '/marathon/modules'


  MarathonModuleLoader(MarathonCoreEngine engine, List<String> paths=[], MarathonPathResource resource = null, boolean coreModule = false, List<Map> initialModules = null) {
    resourceLoader = new ResourceLoader()
    this.engine = engine
    this.resource = resource
    extensionLoaders = new HashMap()
    moduleCache = new HashMap()
    reader = new MarathonPathReader(resource)
    paths.each {
      reader.addPath(it)
    }
    if(!coreModule) {
      loadInitialModules(initialModules)
    }
  }

  private void loadInitialModules(List<Map> initialModules){
    if(initialModules == null){
      loadDefaultModules()
    }else{
      loadCustomInitialModules(initialModules)
    }
  }


  private void loadDefaultModules() {
    List<String> defaultModules = Arrays.asList(resourceLoader.getInputStream("/marathon/modules/defaultLoads").text.split("\n"))
    defaultModules.each {
      loadInitModuleFromResources(it)
    }
  }

  private void loadCustomInitialModules(List<Map> modules){
    modules.each { module ->
      String moduleName = module.name

      if(module.path){
        String modulePath = module.path
        def file = new File(modulePath, "${moduleName}.js")

        if(file.exists()) {
          String code = file.getText()
          MarathonModule result = compileJs(moduleName, code, true)
          moduleCache.put(moduleName, result)
        }
      }else{
        loadInitModuleFromResources(moduleName)
      }
    }
  }

  private void loadInitModuleFromResources(String moduleName){
    def code = resourceLoader.getInputStream("${DEFAULT_MODULES_PATH}/${moduleName}.js").text
    def result = compileJs(moduleName, code, true)
    moduleCache.put(moduleName, result)
  }

  private MarathonPathResource requireMainResource(
    String requirePath,
    MarathonPathResource metaInf,
    MarathonPathResource packageJson,
    MarathonPathResource parentResource = null) {

    def mainPath
    if(metaInf) {
      def manifest = new Manifest(metaInf.inputStream)
      def npmMain = manifest.mainAttributes.getValue("Npm-Main")
      if(npmMain) {
        mainPath = "${requirePath}/${npmMain}" 
      }
    } else if(packageJson) {
      def slurper = new JsonSlurper()
      def json = (Map)slurper.parseText(packageJson.utf8Contents)
      def npmMain = json.main
      if(npmMain) {
        mainPath = "${requirePath}/${npmMain}" 
      }
    }

    if(!mainPath) {
      mainPath = "${requirePath}/index.js"
    }

    return reader.resolvePath(mainPath, parentResource)
  }

  MarathonPathResource requireResource(String requirePath) {
    MarathonPathResource result
    reader.addExtensions(extensionLoaders.keySet())
    result = reader.resolvePath(requirePath)

    if(result && result.isDirectory()) {
      MarathonPathResource metaInf = reader.resolvePath("${requirePath}/META-INF/MANIFEST.MF")
      MarathonPathResource packageJson = reader.resolvePath("${requirePath}/package.json")

      result = requireMainResource(requirePath, metaInf, packageJson, result)
    }

    result
  }

  MarathonModule require(String requirePath) {

    if(moduleCache.containsKey(requirePath)) {
      return moduleCache.get(requirePath)
    }

    MarathonPathResource resource = requireResource(requirePath)

    if(resource) {
      String filename = resource.path.fileName.toString()
      if(filename.endsWith(".js")) {
        MarathonModule result = compileJs(resource)
        moduleCache.put(requirePath, result)
      } else if(filename.endsWith(".json")) {
        MarathonModule result = compileJson(resource)
        moduleCache.put(requirePath, result)
      } else {
        for(extension in extensionLoaders.keySet()) {
          if(filename.endsWith(extension)) {
            Object jsLoader = extensionLoaders.get(extension)
            String fullFilename = resource.path.toAbsolutePath().normalize().toString()
            MarathonContext loaderContext = new MarathonContext()
            loaderContext.loader = this
            loaderContext.put("__fullFilename", jsLoader)
            loaderContext.put("__loaderContext", loaderContext)
            loaderContext.put("__loader", jsLoader)

            def jsModule = engine.eval("""
            {
              "_compile": function loadCustomExtension(source) {
                return __loaderContext.loader.compileJs(__fullFilename, source);
              }
            }
            """, loaderContext)

            MarathonModule result = (MarathonModule)engine.invokeFunction(loaderContext, "__loader", jsModule, fullFilename)
            moduleCache.put(requirePath, result)
            break
          }
        }
      }
      if(moduleCache.containsKey(requirePath)) {
        return moduleCache.get(requirePath)
      }

    }

    throw new IllegalArgumentException("Module ${requirePath} cannot be loaded")
  }

  public MarathonModule compileJs(String filename, String code, boolean coreModule = false) {
    MarathonContext context = new MarathonContext()
    context.scriptName = filename
    if(coreModule) {
      context.loader = this
    } else {
      context.loader = new MarathonModuleLoader(engine, reader.globalPaths, resource, coreModule)
    }
    context.loader.extensionLoaders = extensionLoaders
    context.module = new MarathonModule(context.scriptName)
    engine.evalModule(code, context)
    context.module
  }

  public MarathonModule compileJs(MarathonPathResource resource) {
    MarathonContext context = new MarathonContext()
    context.scriptName = resource.path.fileName.toString()
    context.scriptPath = resource.path.parent.toString()
    context.scriptResource = resource
    context.loader = new MarathonModuleLoader(engine, reader.globalPaths, resource)
    context.loader.extensionLoaders = extensionLoaders
    context.module = new MarathonModule(context.scriptName)
    engine.evalModule(resource.utf8Contents, context)
    context.module
  }

  private MarathonModule compileJson(MarathonPathResource resource) {
    MarathonContext context = new MarathonContext()
    context.scriptName = resource.path.fileName.toString()
    context.scriptPath = resource.path.parent.toString()
    context.scriptResource = resource
    context.loader = new MarathonModuleLoader(engine, reader.globalPaths, resource)
    context.module = new MarathonModule(context.scriptName)
    context.module.moduleMap.exports = engine.eval(resource.utf8Contents, context)
    context.module
  }

  public void setupMarathonPath(List<String> marathonPath) {
    reader.clearPath()

    for(String path : marathonPath) {
      reader.addPath(path)
    }
  }

  public List<String> getMarathonPath() {
    reader.globalPaths
  }

}

