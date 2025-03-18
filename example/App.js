/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, { Component, useState } from 'react';
import { Platform, StyleSheet, Text, TextInput, View, TouchableOpacity, Button, NativeModules, NativeEventEmitter } from 'react-native';
import MopSDK, { Config, FinStoreConfig, BOOLState, LanguageType, FCReLaunchMode, UIConfig } from 'react-native-mopsdk';

const CustomRadioButton = ({onSelectOption}) => {
  const [selectedOption, setSelectedOption] = useState(null);
  
  const options = ['ParamExist', 'OnlyParamDiff', 'Always', 'Never'];

  const handleSelectOption = (option) => {
    setSelectedOption(option);
    onSelectOption(option); 
  };

  return (
    <View>
      {options.map((option, index) => (
        <TouchableOpacity
          key={index}
          style={styles.optionContainer}
          onPress={() => handleSelectOption(option)}
        >
          <View style={styles.radioButton}>
            {selectedOption === option && <View style={styles.radioButtonSelected} />}
          </View>
          <Text style={styles.optionText}>{option}</Text>
        </TouchableOpacity>
      ))}
      {selectedOption && (
        <Text style={styles.selectedText}>Selected Option: {selectedOption}</Text>
      )}
    </View>
  );
};

const options = ['ParamExist', 'OnlyParamDiff', 'Always', 'Never'];

const onPressOpenCanvasApplet = (path, query, selectedOption) => {
  console.log('path:', path);
  console.log('query:', query);
  console.log('Selected Option:', selectedOption);
  
  let startParams = {};

  // 如果 path 不为空，则将其作为 startParams 的一部分
  if (path.trim() !== '') {
    startParams['path'] = path.trim();
  }

  // 如果 query 不为空，则将其作为 startParams 的一部分
  if (query.trim() !== '') {
    startParams['query'] = query.trim();
  }
  
  let mode = options.indexOf(selectedOption)
  MopSDK.startApplet({appletId:'5facb3a52dcbff00017469bd', startParams:startParams, reLaunchMode: mode}).then((res) => {
    console.log('startApplet success: ', res);
  }).catch((error) => {
    console.log('startApplet error: ', error);
  });
};

const onPressOpenDemoApplet = (path, query, selectedOption) => {
  console.log('path:', path);
  console.log('query:', query);
  console.log('Selected Option:', selectedOption);
  
  let startParams = {};

  // 如果 path 不为空，则将其作为 startParams 的一部分
  if (path.trim() !== '') {
    startParams['path'] = path.trim();
  }

  // 如果 query 不为空，则将其作为 startParams 的一部分
  if (query.trim() !== '') {
    startParams['query'] = query.trim();
  }
  
  let mode = options.indexOf(selectedOption)
  MopSDK.startApplet({appletId:'64c23309c533620001a1971e', startParams:startParams, reLaunchMode: mode})
};


const onPressOpenTestApplet = (path, query, selectedOption) => {
  console.log('path:', path);
  console.log('query:', query);
  console.log('Selected Option:', selectedOption);
  
  let startParams = {};

  // 如果 path 不为空，则将其作为 startParams 的一部分
  if (path.trim() !== '') {
    startParams['path'] = path.trim();
  }

  // 如果 query 不为空，则将其作为 startParams 的一部分
  if (query.trim() !== '') {
    startParams['query'] = query.trim();
  }
  
  let mode = options.indexOf(selectedOption)
  MopSDK.startApplet({appletId:'5f17f457297b540001e06ebb', startParams:startParams, reLaunchMode: mode}).then((res) => {
    console.log('startApplet success: ', res);
  }).catch((error) => {
    console.log('startApplet error: ', error);
  });
};

const onPressCustomMenu = () => {
  MopSDK.registerAppletHandler({
    getCustomMenus(appId) {
      console.log("getCustomMenus")
      return []
    }
  })
};

const onPressFinishAll = () => {
  MopSDK.finishAllRunningApplets();
}

const onPressOpenAppletByQRCode = (qrcode, selectedOption) => {
  console.log('qrcode:', qrcode);
  console.log('Selected Option:', selectedOption);
  if (selectedOption == null) {
    selectedOption = options[0];
  }
  let mode = options.indexOf(selectedOption)
  MopSDK.qrcodeOpenApplet(qrcode, false, mode)
}

const onPressGetBindApplets = () => {
  MopSDK.getBindApplets({apiServer:"https://api.finclip.com", pageNo:1, pageSize:20}).then((res) => {
    console.log('getBindApplets success: ', res);
  }).catch((error) => {
    console.error('getBindApplets error: ', error);
  })
}

export default class App extends Component<{}> {

  state = {
    status: 'starting',
    message: '--',
    path: '',
    query: 'key1=value1&age=20',
    qrcode: 'https://api.finclip.com/api/v1/mop/runtime/applet/-f-78d53c04618315e7--',
    selectedOption: 'ParamExist'
  };

  componentDidMount() {

    const eventEmitter = new NativeEventEmitter(NativeModules.FINMopSDK);

    let finStoreConfigA = new FinStoreConfig(
      "22LyZEib0gLTQdU3MUauATBwgfnTCJjdr7FCnywmAEM=",
      "bdfd76cae24d4313",
      "https://api.finclip.com");

    let finStoreConfigs = [finStoreConfigA];
    let config = new Config(finStoreConfigs,
      {language:LanguageType.Chinese,
      userId:"123456",
      channel:"finclip_channel",
      phone:"13012345678",
      debug:true,
      appletDebugMode:BOOLState.BOOLStateTrue}
    ) ;
    let uiConfig = new UIConfig({isHideClearCacheMenu:true});
    let params = {
      config: config,
      uiConfig: uiConfig,
      finMopSDK: NativeModules.FINMopSDK,
      nativeEventEmitter: eventEmitter
    }

    MopSDK.initSDK(params).then((res) => {
      console.log('message: ', res);
      const s = JSON.stringify(res);
      this.setState({
        status: 'native callback received',
        message: s,
      });
    }).catch((error) => {
      console.log('init error: ', error);
      const s = 'initialize fail'
      this.setState({
        status: 'native callback received',
        message: s,
      });
    })
  };

  handleSelectOption = (option) => {
    this.setState({ selectedOption: option });
  };

  render() {
    const { path, query, qrcode, selectedOption } = this.state;
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>☆FINMopsdk example☆</Text>
        <Text style={styles.instructions}>STATUS: {this.state.status}</Text>
        <Text style={styles.welcome}>☆NATIVE CALLBACK MESSAGE☆</Text>
        <Text style={styles.instructions}>{this.state.message}</Text>
        <TextInput style={styles.inputStyle} placeholder="输入path" returnKeyType="done" onChangeText={text => this.setState({path:text})}/>
        <TextInput style={styles.inputStyle} placeholder="输入query" value={query} returnKeyType="done" onChangeText={text => this.setState({query: text})}/>
        <TextInput style={styles.inputStyle} placeholder="输入二维码" value={qrcode} returnKeyType="done" onChangeText={text => this.setState({qrcode:text})}/>
        <CustomRadioButton selectedOption={selectedOption} onSelectOption={this.handleSelectOption}/>
        <Button
          onPress={() => onPressOpenCanvasApplet(this.state.path, this.state.query, this.state.selectedOption)}
          title="打开画图小程序"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={() => onPressOpenDemoApplet(this.state.path, this.state.query, this.state.selectedOption)}
          title="打开官方小程序"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={() => onPressOpenTestApplet(this.state.path, this.state.query, this.state.selectedOption)}
          title="打开测试小程序"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={() => onPressOpenAppletByQRCode(this.state.qrcode, this.state.selectedOption)}
          title="二维码打开小程序"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={onPressFinishAll}
          title="结束所有小程序"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={onPressCustomMenu}
          title="自定义菜单"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
        <Button
          onPress={onPressGetBindApplets}
          title="获取绑定的小程序列表"
          color="#841584"
          accessibilityLabel="Learn more about this purple button"
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  inputStyle: {
    height: 40,
    width:300,
    borderColor: 'gray',
    borderWidth: 1
  },
  optionContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 5,
  },
  radioButton: {
    height: 20,
    width: 20,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: '#000',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 10,
  },
  radioButtonSelected: {
    height: 10,
    width: 10,
    borderRadius: 5,
    backgroundColor: '#000',
  },
  optionText: {
    fontSize: 16,
  },
  selectedText: {
    marginTop: 20,
    fontSize: 18,
    fontWeight: 'bold',
  },
});
