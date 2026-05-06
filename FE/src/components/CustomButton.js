import React from 'react';
import {
  TouchableOpacity,
  Text,
  ActivityIndicator,
  StyleSheet,
} from 'react-native';
import Colors from '../utils/colors';

/**
 * Reusable government-style button component.
 *
 * Props:
 *  - title        : button label
 *  - onPress      : press handler
 *  - loading      : show spinner when true
 *  - variant      : 'primary' | 'outline' | 'danger' (default: 'primary')
 *  - disabled     : disable interaction
 *  - style        : additional container styles
 */
const CustomButton = ({
  title,
  onPress,
  loading = false,
  variant = 'primary',
  disabled = false,
  style,
}) => {
  const isDisabled = disabled || loading;

  const containerStyle = [
    styles.button,
    variant === 'primary' && styles.primary,
    variant === 'outline' && styles.outline,
    variant === 'danger' && styles.danger,
    isDisabled && styles.disabled,
    style,
  ];

  const textStyle = [
    styles.buttonText,
    variant === 'outline' && styles.outlineText,
    variant === 'danger' && styles.dangerText,
  ];

  const spinnerColor =
    variant === 'outline' ? Colors.primary : Colors.textOnPrimary;

  return (
    <TouchableOpacity
      style={containerStyle}
      onPress={onPress}
      disabled={isDisabled}
      activeOpacity={0.8}>
      {loading ? (
        <ActivityIndicator color={spinnerColor} size="small" />
      ) : (
        <Text style={textStyle}>{title}</Text>
      )}
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    height: 50,
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 24,
  },
  primary: {
    backgroundColor: Colors.primary,
  },
  outline: {
    backgroundColor: 'transparent',
    borderWidth: 1.5,
    borderColor: Colors.primary,
  },
  danger: {
    backgroundColor: Colors.error,
  },
  disabled: {
    opacity: 0.55,
  },
  buttonText: {
    color: Colors.textOnPrimary,
    fontSize: 16,
    fontWeight: '600',
    letterSpacing: 0.3,
  },
  outlineText: {
    color: Colors.primary,
  },
  dangerText: {
    color: Colors.textOnPrimary,
  },
});

export default CustomButton;
