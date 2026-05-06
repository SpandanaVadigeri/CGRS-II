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
import {createComplaint} from '../services/api';
import InputField from '../components/InputField';
import CustomButton from '../components/CustomButton';
import Colors from '../utils/colors';

/** Predefined complaint categories */
const CATEGORIES = [
  'Road & Infrastructure',
  'Water Supply',
  'Electricity',
  'Sanitation',
  'Healthcare',
  'Education',
  'Law & Order',
  'Public Transport',
  'Environment',
  'Other',
];

/**
 * Submit Grievance Screen
 *
 * API: POST /complaints   { title, description, category }
 * Requires JWT (attached automatically via api.js interceptor).
 */
const SubmitGrievanceScreen = ({navigation}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [showCategories, setShowCategories] = useState(false);

  const validate = () => {
    const newErrors = {};
    if (!title.trim()) {newErrors.title = 'Title is required';}
    if (!description.trim()) {newErrors.description = 'Description is required';}
    else if (description.trim().length < 20) {newErrors.description = 'Please describe your issue in at least 20 characters';}
    if (!selectedCategory) {newErrors.category = 'Please select a category';}
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) {return;}
    setLoading(true);
    try {
      await createComplaint(title.trim(), description.trim(), selectedCategory);
      Alert.alert(
        'Grievance Submitted',
        'Your grievance has been recorded and will be addressed shortly.',
        [
          {
            text: 'View Grievances',
            onPress: () => navigation.replace('ViewGrievances'),
          },
          {
            text: 'Done',
            onPress: () => navigation.goBack(),
          },
        ],
      );
    } catch (err) {
      const status = err?.response?.status;
      if (status === 401) {
        Alert.alert('Session Expired', 'Please log in again.', [
          {text: 'OK', onPress: () => navigation.replace('Login')},
        ]);
      } else if (!err.response) {
        Alert.alert('Network Error', 'Could not reach the server. Try again.');
      } else {
        Alert.alert('Submission Failed', 'Unable to submit grievance. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setTitle('');
    setDescription('');
    setSelectedCategory('');
    setErrors({});
  };

  return (
    <KeyboardAvoidingView
      style={styles.flex}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.primary} />

      {/* ── Custom Header ───────────────────────────────────────────────── */}
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backBtn}
          activeOpacity={0.7}>
          <Text style={styles.backIcon}>←</Text>
        </TouchableOpacity>
        <View style={styles.headerCenter}>
          <Text style={styles.headerTitle}>Submit Grievance</Text>
          <Text style={styles.headerSubtitle}>File a new complaint</Text>
        </View>
      </View>

      <ScrollView
        contentContainerStyle={styles.body}
        keyboardShouldPersistTaps="handled"
        showsVerticalScrollIndicator={false}>

        {/* ── Info Banner ──────────────────────────────────────────────── */}
        <View style={styles.infoBanner}>
          <Text style={styles.infoBannerText}>
            ℹ️  All grievances are reviewed within 30 working days per the RTI Act.
          </Text>
        </View>

        {/* ── Form ─────────────────────────────────────────────────────── */}
        <View style={styles.formSection}>
          <Text style={styles.sectionTitle}>Grievance Details</Text>

          <InputField
            label="Title"
            value={title}
            onChangeText={setTitle}
            placeholder="Brief title of your issue"
            autoCapitalize="sentences"
            error={errors.title}
          />

          <InputField
            label="Description"
            value={description}
            onChangeText={setDescription}
            placeholder="Describe the issue clearly. Include location, date, and any relevant details..."
            multiline
            numberOfLines={5}
            autoCapitalize="sentences"
            error={errors.description}
          />

          {/* ── Category Picker ──────────────────────────────────────── */}
          <View style={styles.fieldGroup}>
            <Text style={styles.fieldLabel}>Category</Text>
            <TouchableOpacity
              style={[
                styles.categoryPicker,
                errors.category && styles.categoryPickerError,
              ]}
              onPress={() => setShowCategories(!showCategories)}
              activeOpacity={0.8}>
              <Text
                style={[
                  styles.categoryPickerText,
                  !selectedCategory && styles.categoryPickerPlaceholder,
                ]}>
                {selectedCategory || 'Select a category'}
              </Text>
              <Text style={styles.categoryArrow}>
                {showCategories ? '▲' : '▼'}
              </Text>
            </TouchableOpacity>
            {errors.category && (
              <Text style={styles.errorText}>{errors.category}</Text>
            )}

            {showCategories && (
              <View style={styles.categoryDropdown}>
                {CATEGORIES.map(cat => (
                  <TouchableOpacity
                    key={cat}
                    style={[
                      styles.categoryOption,
                      selectedCategory === cat && styles.categoryOptionSelected,
                    ]}
                    onPress={() => {
                      setSelectedCategory(cat);
                      setShowCategories(false);
                      setErrors(prev => ({...prev, category: ''}));
                    }}
                    activeOpacity={0.7}>
                    <Text
                      style={[
                        styles.categoryOptionText,
                        selectedCategory === cat && styles.categoryOptionTextSelected,
                      ]}>
                      {cat}
                    </Text>
                    {selectedCategory === cat && (
                      <Text style={styles.checkmark}>✓</Text>
                    )}
                  </TouchableOpacity>
                ))}
              </View>
            )}
          </View>
        </View>

        {/* ── Action Buttons ───────────────────────────────────────────── */}
        <View style={styles.actions}>
          <CustomButton
            title="Submit Grievance"
            onPress={handleSubmit}
            loading={loading}
          />
          <CustomButton
            title="Clear Form"
            onPress={handleReset}
            variant="outline"
            style={styles.resetBtn}
            disabled={loading}
          />
        </View>

        <Text style={styles.disclaimer}>
          By submitting, you confirm that the information provided is accurate and true to the best of your knowledge.
        </Text>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  flex: {flex: 1, backgroundColor: Colors.surface},

  // ── Header ────────────────────────────────────────────────────────
  header: {
    backgroundColor: Colors.primary,
    flexDirection: 'row',
    alignItems: 'center',
    paddingTop: 48,
    paddingBottom: 18,
    paddingHorizontal: 16,
  },
  backBtn: {
    width: 38,
    height: 38,
    borderRadius: 19,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 14,
  },
  backIcon: {
    fontSize: 18,
    color: Colors.textOnPrimary,
    fontWeight: '700',
  },
  headerCenter: {flex: 1},
  headerTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: Colors.textOnPrimary,
  },
  headerSubtitle: {
    fontSize: 12,
    color: 'rgba(255,255,255,0.75)',
    marginTop: 2,
  },

  // ── Body ──────────────────────────────────────────────────────────
  body: {
    padding: 20,
    paddingBottom: 40,
  },

  // ── Info Banner ───────────────────────────────────────────────────
  infoBanner: {
    backgroundColor: Colors.infoLight,
    borderRadius: 10,
    padding: 14,
    marginBottom: 20,
  },
  infoBannerText: {
    fontSize: 12,
    color: Colors.primary,
    lineHeight: 18,
    fontWeight: '500',
  },

  // ── Form Section ──────────────────────────────────────────────────
  formSection: {
    backgroundColor: Colors.background,
    borderRadius: 12,
    padding: 20,
    borderWidth: 1,
    borderColor: Colors.border,
    marginBottom: 20,
    elevation: 1,
    shadowColor: Colors.shadow,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.05,
    shadowRadius: 3,
  },
  sectionTitle: {
    fontSize: 15,
    fontWeight: '700',
    color: Colors.textPrimary,
    marginBottom: 16,
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: Colors.divider,
  },

  // ── Category Picker ───────────────────────────────────────────────
  fieldGroup: {marginBottom: 16},
  fieldLabel: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
    marginBottom: 6,
    textTransform: 'uppercase',
    letterSpacing: 0.2,
  },
  categoryPicker: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: Colors.inputBackground,
    borderRadius: 8,
    borderWidth: 1.5,
    borderColor: Colors.border,
    paddingHorizontal: 14,
    height: 48,
  },
  categoryPickerError: {borderColor: Colors.error},
  categoryPickerText: {
    fontSize: 15,
    color: Colors.textPrimary,
    flex: 1,
  },
  categoryPickerPlaceholder: {color: Colors.textMuted},
  categoryArrow: {fontSize: 12, color: Colors.textSecondary},
  errorText: {color: Colors.error, fontSize: 12, marginTop: 4},
  categoryDropdown: {
    borderWidth: 1,
    borderColor: Colors.border,
    borderRadius: 8,
    marginTop: 6,
    backgroundColor: Colors.background,
    overflow: 'hidden',
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.1,
    shadowRadius: 6,
  },
  categoryOption: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 13,
    borderBottomWidth: 1,
    borderBottomColor: Colors.divider,
  },
  categoryOptionSelected: {backgroundColor: Colors.infoLight},
  categoryOptionText: {fontSize: 14, color: Colors.textPrimary},
  categoryOptionTextSelected: {color: Colors.primary, fontWeight: '600'},
  checkmark: {color: Colors.primary, fontWeight: '700', fontSize: 15},

  // ── Action Buttons ────────────────────────────────────────────────
  actions: {gap: 12},
  resetBtn: {marginTop: 0},

  // ── Disclaimer ────────────────────────────────────────────────────
  disclaimer: {
    fontSize: 11,
    color: Colors.textMuted,
    textAlign: 'center',
    marginTop: 20,
    lineHeight: 16,
    fontStyle: 'italic',
  },
});

export default SubmitGrievanceScreen;
