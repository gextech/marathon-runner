package gex.marathon.module

import gex.marathon.core.MarathonContext
import gex.marathon.core.MarathonCoreEngine
import gex.marathon.core.MarathonUtils
import gex.marathon.path.MarathonPathReader
import gex.marathon.path.MarathonPathResource

import groovy.transform.CompileStatic

@CompileStatic
class MarathonModuleLoader {
  private Map<String, MarathonModule> moduleCache
  private MarathonPathResource resource
  private MarathonPathReader reader

  Map<String, Object> extensionLoaders

  MarathonModuleLoader(List<String> paths=[], MarathonPathResource resource = null, boolean coreModule = false) {
    extensionLoaders = new HashMap()
    moduleCache = new HashMap()
    reader = new MarathonPathReader(resource)
    paths.each {
      reader.addPath(it)
    }
    if(!coreModule) {
      loadDefaultModules()
    }
  }

  private void loadDefaultModules() {
    List<String> defaultModules = Arrays.asList(MarathonUtils.readResource("/marathon/modules/defaultLoads").split("\n"))
    defaultModules.each {
      def code = MarathonUtils.readResource("/marathon/modules/${it}.js")
      def result = compileJs(it, code, true)
      moduleCache.put(it, result)
    }
  }

  MarathonModule require(String requirePath) {
    if(moduleCache.containsKey(requirePath)) {
      return moduleCache.get(requirePath)
    }

    MarathonPathResource resource = reader.resolvePath(requirePath)
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
            String source = resource.utf8Contents
            Object jsLoader = extensionLoaders.get(extension)
            MarathonCoreEngine loaderEngine = new MarathonCoreEngine()
            MarathonContext loaderContext = new MarathonContext()
            loaderContext.put("loader", jsLoader)

            MarathonModule result = (MarathonModule)loaderEngine.invokeFunction(loaderContext, "loader", filename, source)
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

  private MarathonModule compileJs(String filename, String code, boolean coreModule = false) {
    MarathonContext context = new MarathonContext()
    context.scriptName = filename
    context.loader = new MarathonModuleLoader(reader.globalPaths, resource, coreModule)
    context.loader.extensionLoaders = extensionLoaders
    context.module = new MarathonModule(context.scriptName)
    MarathonCoreEngine engine = new MarathonCoreEngine()
    engine.evalModule(code, context)
    context.module
  }

  private MarathonModule compileJs(MarathonPathResource resource) {
    MarathonContext context = new MarathonContext()
    context.scriptName = resource.path.fileName.toString()
    context.scriptPath = resource.path.parent.toString()
    context.scriptResource = resource
    context.loader = new MarathonModuleLoader(reader.globalPaths, resource)
    context.loader.extensionLoaders = extensionLoaders
    context.module = new MarathonModule(context.scriptName)
    MarathonCoreEngine engine = new MarathonCoreEngine()
    engine.evalModule(resource.utf8Contents, context)
    context.module
  }

  private MarathonModule compileJson(MarathonPathResource resource) {
    MarathonContext context = new MarathonContext()
    context.scriptName = resource.path.fileName.toString()
    context.scriptPath = resource.path.parent.toString()
    context.scriptResource = resource
    context.loader = new MarathonModuleLoader(reader.globalPaths, resource)
    context.module = new MarathonModule(context.scriptName)
    MarathonCoreEngine engine = new MarathonCoreEngine()
    context.module.moduleMap.exports = engine.eval(resource.utf8Contents, context)
    context.module
  }

}

