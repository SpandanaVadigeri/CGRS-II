import AsyncStorage from '@react-native-async-storage/async-storage';

const TOKEN_KEY = 'cgrs_auth_token';
const USER_KEY = 'cgrs_user_data';

/**
 * Save the JWT token to AsyncStorage.
 */
export const saveToken = async (token) => {
  try {
    await AsyncStorage.setItem(TOKEN_KEY, token);
  } catch (error) {
    console.error('Error saving token:', error);
  }
};

/**
 * Retrieve the JWT token from AsyncStorage.
 * Returns null if not found.
 */
export const getToken = async () => {
  try {
    return await AsyncStorage.getItem(TOKEN_KEY);
  } catch (error) {
    console.error('Error getting token:', error);
    return null;
  }
};

/**
 * Remove the JWT token from AsyncStorage (logout).
 */
export const removeToken = async () => {
  try {
    await AsyncStorage.removeItem(TOKEN_KEY);
    await AsyncStorage.removeItem(USER_KEY);
  } catch (error) {
    console.error('Error removing token:', error);
  }
};

/**
 * Save user data (email, role) to AsyncStorage.
 */
export const saveUser = async (userData) => {
  try {
    await AsyncStorage.setItem(USER_KEY, JSON.stringify(userData));
  } catch (error) {
    console.error('Error saving user data:', error);
  }
};

/**
 * Retrieve user data from AsyncStorage.
 * Returns null if not found.
 */
export const getUser = async () => {
  try {
    const data = await AsyncStorage.getItem(USER_KEY);
    return data ? JSON.parse(data) : null;
  } catch (error) {
    console.error('Error getting user data:', error);
    return null;
  }
};
