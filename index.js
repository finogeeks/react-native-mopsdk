import { NativeModules } from 'react-native';

const { FINMopsdk } = NativeModules;
const MopSDK = {
  initialize(params, callback) {
    console.log(params);
    console.log(callback);
    let { appkey, secret, apiServer, apiPrefix } = params;
    if (!appkey || !secret || appkey === '' || secret === '') {
      if (typeof callback === 'function') {
        callback({ succ: false, retMsg: 'appkey、secret不能为空' });
      }
      return;
    }
    if (!apiServer) {
      apiServer = 'https://mp.finogeeks.com';
    }
    if (apiServer.endsWith('/')) {
      apiServer = apiServer.substr(0, apiServer.length() - 1);
    }
    if (!apiPrefix) {
      apiPrefix = '/api/v1/mop';
    }
    if (apiPrefix.endsWith('/')) {
      apiPrefix = apiPrefix.substr(0, apiPrefix.length() - 1);
    }
    console.log({
      appkey: appkey,
      secret: secret,
      apiServer: apiServer,
      apiPrefix: apiPrefix
    })
    FINMopsdk.initialize(appkey, secret, apiServer, apiPrefix, callback);
  },
  openApplet(appId, path, query, sequence, callback) {
    FINMopsdk.openApplet(appId, path, query, sequence, callback);
  }
};
export default MopSDK;
