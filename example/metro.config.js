// metro.config.js
//
// with multiple workarounds for this issue with symlinks:
// https://github.com/facebook/metro/issues/1
//
// with thanks to @johnryan (<https://github.com/johnryan>)
// for the pointers to multiple workaround solutions here:
// https://github.com/facebook/metro/issues/1#issuecomment-541642857
//
// see also this discussion:
// https://github.com/brodybits/create-react-native-module/issues/232

const path = require("path");
const watchFolders = [
  //Relative path to packages directory because I'm in yarn workpspaces
  path.resolve(__dirname + "/../.."),
];
module.exports = {
  resolver: {
    extraNodeModules: new Proxy(
      {},
      { get: (_, name) => path.resolve('.', 'node_modules', name) }
    )
  },
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        // this defeats the RCTDeviceEventEmitter is not a registered callable module
        inlineRequires: true,
      },
    }),
  },
  watchFolders,
};
