import React, {useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  StatusBar,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import {registerUser} from '../services/api';
import InputField from '../components/InputField';
import CustomButton from '../components/CustomButton';
import Colors from '../utils/colors';

/** Available roles a new user can select */
const ROLES = ['CITIZEN', 'AUTHORITY', 'ADMIN'];

/**
 * Register Screen
 *
 * API: POST /auth/register → { message, name, email, role }  (no token!)
 * On success: show success alert → navigate to Login
 */
const RegisterScreen = ({navigation}) => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [selectedRole, setSelectedRole] = useState('CITIZEN');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!name.trim()) {newErrors.name = 'Full name is required';}
    if (!email.trim()) {newErrors.email = 'Email is required';}
    else if (!/\S+@\S+\.\S+/.test(email)) {newErrors.email = 'Enter a valid email';}
    if (!password) {newErrors.password = 'Password is required';}
    else if (password.length < 6) {newErrors.password = 'Minimum 6 characters';}
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validate()) {return;}
    setLoading(true);
    try {
      await registerUser(name.trim(), email.trim().toLowerCase(), password, selectedRole);
      Alert.alert(
        'Registration Successful',
        'Your account has been created. You can now sign in.',
        [{text: 'Sign In', onPress: () => navigation.replace('Login')}],
      );
    } catch (err) {
      const status = err?.response?.status;
      const message = err?.response?.data?.message;
      if (status === 409 || (message && message.toLowerCase().includes('exist'))) {
        Alert.alert('Registration Failed', 'An account with this email already exists.');
      } else if (!err.response) {
        Alert.alert('Network Error', 'Unable to reach the server. Check your connection.');
      } else {
        Alert.alert('Error', message || 'Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.primary} />
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled">

        {/* ── Header ──────────────────────────────────────────────────── */}
        <View style={styles.banner}>
          <View style={styles.backRow}>
            <TouchableOpacity
              onPress={() => navigation.goBack()}
              style={styles.backBtn}
              activeOpacity={0.7}>
              <Text style={styles.backIcon}>←</Text>
            </TouchableOpacity>
          </View>
          <View style={styles.emblemCircle}>
            <Text style={styles.emblemText}>🏛️</Text>
          </View>
          <Text style={styles.appTitle}>Create Account</Text>
          <Text style={styles.appSubtitle}>
            Register as a citizen or official
          </Text>
        </View>

        {/* ── Form Card ───────────────────────────────────────────────── */}
        <View style={styles.formCard}>
          <InputField
            label="Full Name"
            value={name}
            onChangeText={setName}
            placeholder="Rajesh Kumar"
            autoCapitalize="words"
            error={errors.name}
          />

          <InputField
            label="Email Address"
            value={email}
            onChangeText={setEmail}
            placeholder="you@example.com"
            keyboardType="email-address"
            autoCapitalize="none"
            error={errors.email}
          />

          <InputField
            label="Password"
            value={password}
            onChangeText={setPassword}
            placeholder="Min. 6 characters"
            secureTextEntry
            error={errors.password}
          />

          {/* ── Role Selector ─────────────────────────────────────────── */}
          <View style={styles.roleSection}>
            <Text style={styles.roleLabel}>Select Role</Text>
            <View style={styles.roleRow}>
              {ROLES.map(role => {
                const isSelected = selectedRole === role;
                return (
                  <TouchableOpacity
                    key={role}
                    style={[styles.roleChip, isSelected && styles.roleChipSelected]}
                    onPress={() => setSelectedRole(role)}
                    activeOpacity={0.7}>
                    <Text
                      style={[
                        styles.roleChipText,
                        isSelected && styles.roleChipTextSelected,
                      ]}>
                      {role}
                    </Text>
                  </TouchableOpacity>
                );
              })}
            </View>
            <Text style={styles.roleHint}>
              {selectedRole === 'CITIZEN'
                ? 'Submit and track your grievances'
                : selectedRole === 'AUTHORITY'
                ? 'Manage and resolve grievances in your department'
                : 'Full administrative access'}
            </Text>
          </View>

          <CustomButton
            title="Create Account"
            onPress={handleRegister}
            loading={loading}
            style={styles.registerBtn}
          />

          <TouchableOpacity
            onPress={() => navigation.navigate('Login')}
            style={styles.loginLink}
            activeOpacity={0.7}>
            <Text style={styles.loginText}>
              Already have an account?{' '}
              <Text style={styles.loginTextBold}>Sign In</Text>
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  flex: {flex: 1, backgroundColor: Colors.primary},

  container: {
    flexGrow: 1,
    backgroundColor: Colors.surface,
  },

  // ── Banner ──────────────────────────────────────────────────────────
  banner: {
    backgroundColor: Colors.primary,
    alignItems: 'center',
    paddingTop: 48,
    paddingBottom: 36,
    paddingHorizontal: 24,
  },
  backRow: {
    width: '100%',
    marginBottom: 8,
  },
  backBtn: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  backIcon: {
    fontSize: 18,
    color: Colors.textOnPrimary,
    fontWeight: '700',
  },
  emblemCircle: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
  },
  emblemText: {fontSize: 28},
  appTitle: {
    fontSize: 24,
    fontWeight: '800',
    color: Colors.textOnPrimary,
    letterSpacing: 0.5,
  },
  appSubtitle: {
    fontSize: 13,
    color: 'rgba(255,255,255,0.8)',
    marginTop: 4,
  },

  // ── Form Card ────────────────────────────────────────────────────────
  formCard: {
    flex: 1,
    backgroundColor: Colors.background,
    marginTop: -16,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    padding: 28,
    paddingTop: 32,
  },

  // ── Role Selector ────────────────────────────────────────────────────
  roleSection: {marginBottom: 20},
  roleLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
    marginBottom: 10,
    letterSpacing: 0.2,
    textTransform: 'uppercase',
  },
  roleRow: {
    flexDirection: 'row',
    gap: 10,
    flexWrap: 'wrap',
  },
  roleChip: {
    paddingHorizontal: 16,
    paddingVertical: 9,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: Colors.border,
    backgroundColor: Colors.inputBackground,
  },
  roleChipSelected: {
    backgroundColor: Colors.primary,
    borderColor: Colors.primary,
  },
  roleChipText: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
  },
  roleChipTextSelected: {
    color: Colors.textOnPrimary,
  },
  roleHint: {
    fontSize: 12,
    color: Colors.textMuted,
    marginTop: 8,
    fontStyle: 'italic',
  },

  registerBtn: {marginTop: 4},

  // ── Login Link ───────────────────────────────────────────────────────
  loginLink: {alignItems: 'center', paddingVertical: 16},
  loginText: {fontSize: 14, color: Colors.textSecondary},
  loginTextBold: {color: Colors.primary, fontWeight: '700'},
});

export default RegisterScreen;
