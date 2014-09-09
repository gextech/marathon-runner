var imports = new JavaImporter(java.nio.file);

with(imports) {
  module.exports = {
    dirname: function (filename) {
      var path = FileSystems.getDefault().getPath(filename);
      return path.getParent().toString();
    },
    basename: function (filename) {
      var path = FileSystems.getDefault().getPath(filename);
      return path.getFileName().toString();
    }
  };
}
