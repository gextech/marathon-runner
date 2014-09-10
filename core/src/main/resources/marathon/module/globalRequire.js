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
