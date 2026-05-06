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
import {loginUser} from '../services/api';
import {saveToken, saveUser} from '../storage/authStorage';
import InputField from '../components/InputField';
import CustomButton from '../components/CustomButton';
import Colors from '../utils/colors';

/**
 * Login Screen
 *
 * API: POST /auth/login → { token, email, role }
 * On success: save token + user, navigate to Dashboard
 */
const LoginScreen = ({navigation}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!email.trim()) {newErrors.email = 'Email is required';}
    else if (!/\S+@\S+\.\S+/.test(email)) {newErrors.email = 'Enter a valid email';}
    if (!password) {newErrors.password = 'Password is required';}
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleLogin = async () => {
    if (!validate()) {return;}
    setLoading(true);
    try {
      const res = await loginUser(email.trim().toLowerCase(), password);
      const {token, email: userEmail, role} = res.data;

      // Persist token and user data
      await saveToken(token);
      await saveUser({email: userEmail, role});

      // Navigate to Dashboard, passing user info
      navigation.replace('Dashboard', {email: userEmail, role});
    } catch (err) {
      const status = err?.response?.status;
      if (status === 401 || status === 403) {
        Alert.alert('Login Failed', 'Invalid email or password. Please try again.');
      } else if (!err.response) {
        Alert.alert('Network Error', 'Unable to reach the server. Check your connection.');
      } else {
        Alert.alert('Error', 'An unexpected error occurred. Please try again.');
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

        {/* ── Header Banner ──────────────────────────────────────────── */}
        <View style={styles.banner}>
          <View style={styles.emblemCircle}>
            <Text style={styles.emblemText}>🏛️</Text>
          </View>
          <Text style={styles.appTitle}>CGRS</Text>
          <Text style={styles.appSubtitle}>
            Citizen Grievance Redressal System
          </Text>
          <Text style={styles.govLabel}>Government of India</Text>
        </View>

        {/* ── Form Card ──────────────────────────────────────────────── */}
        <View style={styles.formCard}>
          <Text style={styles.formTitle}>Sign In</Text>
          <Text style={styles.formSubtitle}>
            Enter your credentials to continue
          </Text>

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
            placeholder="Enter your password"
            secureTextEntry
            error={errors.password}
          />

          <CustomButton
            title="Sign In"
            onPress={handleLogin}
            loading={loading}
            style={styles.loginBtn}
          />

          <View style={styles.divider}>
            <View style={styles.dividerLine} />
            <Text style={styles.dividerText}>OR</Text>
            <View style={styles.dividerLine} />
          </View>

          <TouchableOpacity
            onPress={() => navigation.navigate('Register')}
            style={styles.registerLink}
            activeOpacity={0.7}>
            <Text style={styles.registerText}>
              New user?{' '}
              <Text style={styles.registerTextBold}>Create an Account</Text>
            </Text>
          </TouchableOpacity>
        </View>

        {/* ── Footer ─────────────────────────────────────────────────── */}
        <Text style={styles.footer}>
          © 2024 Ministry of Electronics &amp; IT. All rights reserved.
        </Text>
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
    paddingTop: 60,
    paddingBottom: 40,
    paddingHorizontal: 24,
  },
  emblemCircle: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  emblemText: {fontSize: 36},
  appTitle: {
    fontSize: 30,
    fontWeight: '800',
    color: Colors.textOnPrimary,
    letterSpacing: 2,
  },
  appSubtitle: {
    fontSize: 13,
    color: 'rgba(255,255,255,0.85)',
    marginTop: 4,
    textAlign: 'center',
    letterSpacing: 0.3,
  },
  govLabel: {
    fontSize: 11,
    color: 'rgba(255,255,255,0.6)',
    marginTop: 6,
    textTransform: 'uppercase',
    letterSpacing: 1.5,
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
  formTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: Colors.textPrimary,
    marginBottom: 4,
  },
  formSubtitle: {
    fontSize: 13,
    color: Colors.textSecondary,
    marginBottom: 28,
  },
  loginBtn: {marginTop: 8},

  // ── Divider ──────────────────────────────────────────────────────────
  divider: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: 20,
  },
  dividerLine: {
    flex: 1,
    height: 1,
    backgroundColor: Colors.border,
  },
  dividerText: {
    marginHorizontal: 12,
    fontSize: 12,
    color: Colors.textMuted,
    fontWeight: '600',
  },

  // ── Register Link ────────────────────────────────────────────────────
  registerLink: {alignItems: 'center', paddingVertical: 8},
  registerText: {fontSize: 14, color: Colors.textSecondary},
  registerTextBold: {color: Colors.primary, fontWeight: '700'},

  // ── Footer ───────────────────────────────────────────────────────────
  footer: {
    textAlign: 'center',
    fontSize: 11,
    color: Colors.textMuted,
    paddingVertical: 24,
    paddingHorizontal: 16,
    backgroundColor: Colors.background,
  },
});

export default LoginScreen;
