import React from 'react';
import {View, Text, TouchableOpacity, StyleSheet} from 'react-native';
import Colors from '../utils/colors';

/**
 * Returns status badge color config given a complaint status string.
 */
const getStatusConfig = (status) => {
  switch (status?.toUpperCase()) {
    case 'PENDING':
      return {
        bg: Colors.statusPendingBg,
        text: Colors.statusPending,
        label: 'Pending',
      };
    case 'IN_PROGRESS':
      return {
        bg: Colors.statusInProgressBg,
        text: Colors.statusInProgress,
        label: 'In Progress',
      };
    case 'RESOLVED':
      return {
        bg: Colors.statusResolvedBg,
        text: Colors.statusResolved,
        label: 'Resolved',
      };
    default:
      return {
        bg: Colors.surface,
        text: Colors.textSecondary,
        label: status || 'Unknown',
      };
  }
};

/**
 * Grievance Card — displays complaint summary in a list.
 *
 * Props:
 *  - item     : complaint object { id, title, description, category, status }
 *  - onPress  : optional press handler for detail view
 */
const GrievanceCard = ({item, onPress}) => {
  const statusConfig = getStatusConfig(item?.status);

  return (
    <TouchableOpacity
      style={styles.card}
      onPress={onPress}
      activeOpacity={onPress ? 0.75 : 1}>
      {/* ── Header row ───────────────────────────────────────────────── */}
      <View style={styles.header}>
        <Text style={styles.title} numberOfLines={1}>
          {item?.title || 'Untitled'}
        </Text>
        <View style={[styles.badge, {backgroundColor: statusConfig.bg}]}>
          <Text style={[styles.badgeText, {color: statusConfig.text}]}>
            {statusConfig.label}
          </Text>
        </View>
      </View>

      {/* ── Description ──────────────────────────────────────────────── */}
      <Text style={styles.description} numberOfLines={2}>
        {item?.description || 'No description provided.'}
      </Text>

      {/* ── Footer ───────────────────────────────────────────────────── */}
      <View style={styles.footer}>
        <View style={styles.categoryChip}>
          <Text style={styles.categoryText}>
            {item?.category || 'General'}
          </Text>
        </View>
        <Text style={styles.idText}>#{item?.id}</Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: Colors.cardBackground,
    borderRadius: 10,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: Colors.border,
    // Android shadow
    elevation: 2,
    // iOS shadow
    shadowColor: Colors.shadow,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.08,
    shadowRadius: 4,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 8,
    gap: 8,
  },
  title: {
    flex: 1,
    fontSize: 15,
    fontWeight: '700',
    color: Colors.textPrimary,
    lineHeight: 20,
  },
  badge: {
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderRadius: 20,
    alignSelf: 'flex-start',
  },
  badgeText: {
    fontSize: 11,
    fontWeight: '700',
    textTransform: 'uppercase',
    letterSpacing: 0.4,
  },
  description: {
    fontSize: 13,
    color: Colors.textSecondary,
    lineHeight: 19,
    marginBottom: 12,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  categoryChip: {
    backgroundColor: Colors.badge,
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderRadius: 6,
  },
  categoryText: {
    fontSize: 12,
    color: Colors.badgeText,
    fontWeight: '600',
  },
  idText: {
    fontSize: 12,
    color: Colors.textMuted,
    fontWeight: '500',
  },
});

export default GrievanceCard;
