(function() {
  var __filename = __marathon.context.scriptName;
  var __dirname = __marathon.context.scriptPath;
  var require = function require(requirePath) {
    var loader = __marathon.context.loader;
    return loader.require(requirePath).exports;
  };
