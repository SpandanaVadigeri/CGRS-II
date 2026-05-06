import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';

import LoginScreen from '../screens/LoginScreen';
import RegisterScreen from '../screens/RegisterScreen';
import DashboardScreen from '../screens/DashboardScreen';
import SubmitGrievanceScreen from '../screens/SubmitGrievanceScreen';
import ViewGrievancesScreen from '../screens/ViewGrievancesScreen';

const Stack = createStackNavigator();

/**
 * Root navigator for the CGRS mobile app.
 *
 * All headers are hidden — each screen implements its own styled header.
 *
 * Flow:
 *   Login  →  Dashboard  →  SubmitGrievance
 *                       →  ViewGrievances
 *   Login  →  Register  →  Login
 */
const AppNavigator = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Login"
        screenOptions={{
          headerShown: false,
          animation: 'slide_from_right',
          gestureEnabled: true,
        }}>
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="Register" component={RegisterScreen} />
        <Stack.Screen name="Dashboard" component={DashboardScreen} />
        <Stack.Screen name="SubmitGrievance" component={SubmitGrievanceScreen} />
        <Stack.Screen name="ViewGrievances" component={ViewGrievancesScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
