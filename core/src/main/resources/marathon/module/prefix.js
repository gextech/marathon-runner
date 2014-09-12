(function() {
  var __filename = __marathon.context.scriptName;
  var __dirname = __marathon.context.scriptPath;
  var require = function require(requirePath) {
    var loader = __marathon.context.loader;
    if(!loader) {
      throw Error('Cannot require, loader is not defined');
    }
    return loader.require(requirePath).exports;
  };
  require.resolve = function(requirePath) {
    var loader = __marathon.context.loader;
    var resource = loader.requireResource(requirePath);
    if(resource) {
      return resource.path.toAbsolutePath().toString();
    }
  };
  if(__marathon.context.loader) {
    require.extensions = __marathon.context.loader.extensionLoaders;
  }
