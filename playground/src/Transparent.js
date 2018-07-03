import React, { Component } from 'react';
import { TouchableOpacity, Text, View, ScrollView, Platform, Image } from 'react-native';

import styles from './Styles';

export default class Transparent extends Component {
  static navigationItem = {
    screenColor: '#00000000',
    passThroughTouches: true,
  };

  constructor(props) {
    super(props);
  }

  log() {
    console.info('Transparent !!');
  }

  componentDidMount() {
    console.info('componentDidMount transparent');
  }

  componentWillUnmount() {
    console.info('componentWillUnmount transparent');
  }

  render() {
    return (
      <View style={{ flex: 1 }}>
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
          <View style={styles.transparent}>
            <Text>{this.props.text}</Text>
            <TouchableOpacity onPress={this.log} activeOpacity={0.2} style={styles.button}>
              <Text style={styles.buttonText}>点我</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    );
  }
}
