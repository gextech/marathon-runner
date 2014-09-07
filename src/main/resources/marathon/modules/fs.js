module.exports = {
  readFileSync: function (filename, charset) {
    var loader = __marathon.context.loader;
    var resource = loader.requireResource(filename);
    if(charset) {
      return resource.getStringContents(charset);
    } else {
      throw Error("Buffer reading is not implemented yet");
    }
  }
};
