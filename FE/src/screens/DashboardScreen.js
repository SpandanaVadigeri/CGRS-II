import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  StatusBar,
} from 'react-native';
import {removeToken} from '../storage/authStorage';
import Colors from '../utils/colors';

/** Icon + label for each dashboard action card */
const ACTION_CARDS = {
  CITIZEN: [
    {
      id: 'submit',
      icon: '📝',
      title: 'Submit Grievance',
      subtitle: 'File a new complaint or concern',
      screen: 'SubmitGrievance',
      color: Colors.primary,
    },
    {
      id: 'view',
      icon: '📋',
      title: 'View Grievances',
      subtitle: 'Track status of your submissions',
      screen: 'ViewGrievances',
      color: Colors.success,
    },
  ],
  AUTHORITY: [
    {
      id: 'view',
      icon: '📋',
      title: 'All Grievances',
      subtitle: 'Review and manage complaints',
      screen: 'ViewGrievances',
      color: Colors.primary,
    },
  ],
  ADMIN: [
    {
      id: 'view',
      icon: '📋',
      title: 'All Grievances',
      subtitle: 'Full system-wide complaint list',
      screen: 'ViewGrievances',
      color: Colors.primary,
    },
    {
      id: 'submit',
      icon: '📝',
      title: 'Submit Grievance',
      subtitle: 'File a complaint on behalf of a citizen',
      screen: 'SubmitGrievance',
      color: Colors.success,
    },
  ],
};

/** Maps role string to a human-readable label */
const roleLabel = (role) => {
  switch (role) {
    case 'CITIZEN': return 'Citizen';
    case 'AUTHORITY': return 'Authority Officer';
    case 'ADMIN': return 'Administrator';
    default: return role;
  }
};

/** Maps role string to badge colors */
const roleBadgeStyle = (role) => {
  switch (role) {
    case 'CITIZEN':   return {bg: Colors.infoLight, text: Colors.primary};
    case 'AUTHORITY': return {bg: Colors.warningLight, text: Colors.warning};
    case 'ADMIN':     return {bg: Colors.errorLight, text: Colors.error};
    default:          return {bg: Colors.surface, text: Colors.textSecondary};
  }
};

/**
 * Dashboard Screen
 *
 * Shown after login. Displays role-appropriate action cards.
 * Route params: { email, role }
 */
const DashboardScreen = ({navigation, route}) => {
  const {email = 'user@example.com', role = 'CITIZEN'} = route.params || {};

  const cards = ACTION_CARDS[role] ?? ACTION_CARDS.CITIZEN;
  const badge = roleBadgeStyle(role);

  const handleLogout = () => {
    Alert.alert(
      'Sign Out',
      'Are you sure you want to sign out?',
      [
        {text: 'Cancel', style: 'cancel'},
        {
          text: 'Sign Out',
          style: 'destructive',
          onPress: async () => {
            await removeToken();
            navigation.replace('Login');
          },
        },
      ],
    );
  };

  return (
    <View style={styles.flex}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.primary} />

      {/* ── Top Header ─────────────────────────────────────────────────── */}
      <View style={styles.header}>
        <View style={styles.headerLeft}>
          <Text style={styles.headerGreeting}>Welcome back,</Text>
          <Text style={styles.headerEmail} numberOfLines={1}>
            {email}
          </Text>
        </View>
        <TouchableOpacity
          onPress={handleLogout}
          style={styles.logoutBtn}
          activeOpacity={0.7}>
          <Text style={styles.logoutIcon}>⏻</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.body} showsVerticalScrollIndicator={false}>

        {/* ── Identity Card ───────────────────────────────────────────── */}
        <View style={styles.identityCard}>
          <View style={styles.avatarCircle}>
            <Text style={styles.avatarText}>
              {email?.charAt(0)?.toUpperCase() ?? 'U'}
            </Text>
          </View>
          <View style={styles.identityInfo}>
            <Text style={styles.identityEmail}>{email}</Text>
            <View style={[styles.roleBadge, {backgroundColor: badge.bg}]}>
              <Text style={[styles.roleBadgeText, {color: badge.text}]}>
                {roleLabel(role)}
              </Text>
            </View>
          </View>
        </View>

        {/* ── Section Title ────────────────────────────────────────────── */}
        <View style={styles.sectionHeader}>
          <View style={styles.sectionAccent} />
          <Text style={styles.sectionTitle}>Quick Actions</Text>
        </View>

        {/* ── Action Cards ─────────────────────────────────────────────── */}
        {cards.map(card => (
          <TouchableOpacity
            key={card.id}
            style={styles.actionCard}
            onPress={() => navigation.navigate(card.screen)}
            activeOpacity={0.8}>
            <View style={[styles.cardIconBox, {backgroundColor: card.color + '18'}]}>
              <Text style={styles.cardIcon}>{card.icon}</Text>
            </View>
            <View style={styles.cardContent}>
              <Text style={styles.cardTitle}>{card.title}</Text>
              <Text style={styles.cardSubtitle}>{card.subtitle}</Text>
            </View>
            <Text style={[styles.cardArrow, {color: card.color}]}>›</Text>
          </TouchableOpacity>
        ))}

        {/* ── Info Strip ───────────────────────────────────────────────── */}
        <View style={styles.infoStrip}>
          <Text style={styles.infoText}>
            🔒  Your data is securely managed by the Government of India
          </Text>
        </View>

      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  flex: {flex: 1, backgroundColor: Colors.surface},

  // ── Header ─────────────────────────────────────────────────────────
  header: {
    backgroundColor: Colors.primary,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerLeft: {flex: 1},
  headerGreeting: {
    fontSize: 13,
    color: 'rgba(255,255,255,0.7)',
    letterSpacing: 0.3,
  },
  headerEmail: {
    fontSize: 16,
    fontWeight: '700',
    color: Colors.textOnPrimary,
    marginTop: 2,
  },
  logoutBtn: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 12,
  },
  logoutIcon: {fontSize: 18, color: Colors.textOnPrimary},

  // ── Body ─────────────────────────────────────────────────────────
  body: {
    padding: 20,
    paddingBottom: 36,
  },

  // ── Identity Card ────────────────────────────────────────────────
  identityCard: {
    backgroundColor: Colors.background,
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 24,
    borderWidth: 1,
    borderColor: Colors.border,
    elevation: 2,
    shadowColor: Colors.shadow,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.06,
    shadowRadius: 4,
  },
  avatarCircle: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: Colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 14,
  },
  avatarText: {
    fontSize: 20,
    fontWeight: '800',
    color: Colors.textOnPrimary,
  },
  identityInfo: {flex: 1},
  identityEmail: {
    fontSize: 14,
    fontWeight: '600',
    color: Colors.textPrimary,
    marginBottom: 6,
  },
  roleBadge: {
    alignSelf: 'flex-start',
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderRadius: 20,
  },
  roleBadgeText: {
    fontSize: 11,
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },

  // ── Section Header ────────────────────────────────────────────────
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 14,
  },
  sectionAccent: {
    width: 4,
    height: 18,
    borderRadius: 2,
    backgroundColor: Colors.primary,
    marginRight: 10,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: Colors.textPrimary,
    letterSpacing: 0.2,
  },

  // ── Action Cards ──────────────────────────────────────────────────
  actionCard: {
    backgroundColor: Colors.background,
    borderRadius: 12,
    padding: 18,
    marginBottom: 14,
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: Colors.border,
    elevation: 2,
    shadowColor: Colors.shadow,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.06,
    shadowRadius: 4,
  },
  cardIconBox: {
    width: 52,
    height: 52,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 16,
  },
  cardIcon: {fontSize: 24},
  cardContent: {flex: 1},
  cardTitle: {
    fontSize: 15,
    fontWeight: '700',
    color: Colors.textPrimary,
    marginBottom: 3,
  },
  cardSubtitle: {
    fontSize: 13,
    color: Colors.textSecondary,
    lineHeight: 18,
  },
  cardArrow: {
    fontSize: 28,
    fontWeight: '300',
    marginLeft: 8,
  },

  // ── Info Strip ────────────────────────────────────────────────────
  infoStrip: {
    backgroundColor: Colors.infoLight,
    borderRadius: 10,
    padding: 14,
    marginTop: 8,
  },
  infoText: {
    fontSize: 12,
    color: Colors.primary,
    textAlign: 'center',
    lineHeight: 18,
    fontWeight: '500',
  },
});

export default DashboardScreen;
