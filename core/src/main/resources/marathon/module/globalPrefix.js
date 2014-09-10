var __filename = __marathon.context.scriptName;
var __dirname = __marathon.context.scriptPath;
var require = function require(requirePath) {
  var loader = __marathon.context.loader;
  if(!loader) {
    throw Error('Cannot require, loader is not defined');
  }
  return loader.require(requirePath).exports;
};
if(__marathon.context.loader) {
  require.extensions = __marathon.context.loader.extensionLoaders;
}

