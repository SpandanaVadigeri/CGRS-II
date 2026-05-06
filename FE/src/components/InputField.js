import React, {useState} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
} from 'react-native';
import Colors from '../utils/colors';

/**
 * Reusable labeled input field with optional password visibility toggle.
 *
 * Props:
 *  - label           : field label text
 *  - value           : controlled value
 *  - onChangeText    : change handler
 *  - placeholder     : placeholder text
 *  - secureTextEntry : hides text (password mode)
 *  - keyboardType    : React Native keyboardType
 *  - autoCapitalize  : 'none' | 'words' | 'sentences' | 'characters'
 *  - error           : error message string
 *  - editable        : whether the field is editable
 *  - multiline       : allow multiple lines
 *  - numberOfLines   : number of visible lines (multiline)
 */
const InputField = ({
  label,
  value,
  onChangeText,
  placeholder,
  secureTextEntry = false,
  keyboardType = 'default',
  autoCapitalize = 'none',
  error,
  editable = true,
  multiline = false,
  numberOfLines = 1,
}) => {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const isSecure = secureTextEntry && !passwordVisible;

  return (
    <View style={styles.container}>
      {label ? <Text style={styles.label}>{label}</Text> : null}
      <View
        style={[
          styles.inputWrapper,
          error && styles.inputError,
          !editable && styles.inputDisabled,
        ]}>
        <TextInput
          style={[styles.input, multiline && styles.multilineInput]}
          value={value}
          onChangeText={onChangeText}
          placeholder={placeholder}
          placeholderTextColor={Colors.textMuted}
          secureTextEntry={isSecure}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
          editable={editable}
          multiline={multiline}
          numberOfLines={numberOfLines}
          textAlignVertical={multiline ? 'top' : 'center'}
        />
        {secureTextEntry && (
          <TouchableOpacity
            onPress={() => setPasswordVisible(!passwordVisible)}
            style={styles.eyeButton}
            activeOpacity={0.7}>
            <Text style={styles.eyeIcon}>
              {passwordVisible ? '🙈' : '👁️'}
            </Text>
          </TouchableOpacity>
        )}
      </View>
      {error ? <Text style={styles.errorText}>{error}</Text> : null}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    marginBottom: 16,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
    marginBottom: 6,
    letterSpacing: 0.2,
    textTransform: 'uppercase',
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: Colors.inputBackground,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: Colors.border,
    paddingHorizontal: 14,
  },
  inputError: {
    borderColor: Colors.error,
  },
  inputDisabled: {
    opacity: 0.6,
  },
  input: {
    flex: 1,
    fontSize: 15,
    color: Colors.textPrimary,
    height: 48,
    paddingVertical: 0,
  },
  multilineInput: {
    height: 100,
    paddingTop: 12,
    paddingBottom: 12,
  },
  eyeButton: {
    padding: 4,
  },
  eyeIcon: {
    fontSize: 16,
  },
  errorText: {
    color: Colors.error,
    fontSize: 12,
    marginTop: 4,
    marginLeft: 2,
  },
});

export default InputField;
