/**
 * CGRS Mobile App — Root Component
 *
 * Renders the AppNavigator which manages all screens and navigation.
 * GestureHandlerRootView is required as the outermost wrapper for
 * react-native-gesture-handler (used by @react-navigation/stack).
 */
import React from 'react';
import {StyleSheet} from 'react-native';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import AppNavigator from './src/navigation/AppNavigator';

const App = () => {
  return (
    <GestureHandlerRootView style={styles.root}>
      <AppNavigator />
    </GestureHandlerRootView>
  );
};

const styles = StyleSheet.create({
  root: {flex: 1},
});

export default App;
