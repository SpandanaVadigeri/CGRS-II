import React, {useState, useEffect, useCallback} from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Alert,
  StatusBar,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import {getAllComplaints} from '../services/api';
import GrievanceCard from '../components/GrievanceCard';
import Colors from '../utils/colors';

/** Filter tab options */
const FILTERS = ['All', 'PENDING', 'IN_PROGRESS', 'RESOLVED'];

const filterLabel = (f) => {
  switch (f) {
    case 'All': return 'All';
    case 'PENDING': return 'Pending';
    case 'IN_PROGRESS': return 'In Progress';
    case 'RESOLVED': return 'Resolved';
    default: return f;
  }
};

/**
 * View Grievances Screen
 *
 * API: GET /complaints  — returns array of complaint objects
 * Requires JWT (auto-attached by interceptor).
 */
const ViewGrievancesScreen = ({navigation}) => {
  const [complaints, setComplaints] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [activeFilter, setActiveFilter] = useState('All');
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState(null);

  const fetchComplaints = useCallback(async (isRefresh = false) => {
    if (isRefresh) {setRefreshing(true);}
    else {setLoading(true);}
    setError(null);
    try {
      const res = await getAllComplaints();
      const data = res.data ?? [];
      setComplaints(data);
      applyFilter(activeFilter, data);
    } catch (err) {
      const status = err?.response?.status;
      if (status === 401) {
        Alert.alert('Session Expired', 'Please log in again.', [
          {text: 'OK', onPress: () => navigation.replace('Login')},
        ]);
      } else {
        setError('Failed to load grievances. Pull down to try again.');
      }
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [activeFilter, navigation]);

  useEffect(() => {
    fetchComplaints();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const applyFilter = (filter, data = complaints) => {
    setActiveFilter(filter);
    if (filter === 'All') {
      setFiltered(data);
    } else {
      setFiltered(data.filter(c => c.status === filter));
    }
  };

  const handleFilterPress = (filter) => {
    applyFilter(filter);
  };

  // ── Stat counts ──────────────────────────────────────────────────
  const stats = {
    total: complaints.length,
    pending: complaints.filter(c => c.status === 'PENDING').length,
    inProgress: complaints.filter(c => c.status === 'IN_PROGRESS').length,
    resolved: complaints.filter(c => c.status === 'RESOLVED').length,
  };

  // ── Render helpers ───────────────────────────────────────────────

  const renderEmpty = () => (
    <View style={styles.emptyState}>
      <Text style={styles.emptyIcon}>📭</Text>
      <Text style={styles.emptyTitle}>
        {activeFilter === 'All' ? 'No Grievances Found' : `No ${filterLabel(activeFilter)} Grievances`}
      </Text>
      <Text style={styles.emptySubtitle}>
        {activeFilter === 'All'
          ? 'You have not submitted any grievances yet.'
          : `No complaints with status "${filterLabel(activeFilter)}".`}
      </Text>
    </View>
  );

  const renderHeader = () => (
    <>
      {/* ── Summary Stats ──────────────────────────────────────────── */}
      <View style={styles.statsRow}>
        <StatChip label="Total" value={stats.total} color={Colors.primary} />
        <StatChip label="Pending" value={stats.pending} color={Colors.statusPending} />
        <StatChip label="In Progress" value={stats.inProgress} color={Colors.statusInProgress} />
        <StatChip label="Resolved" value={stats.resolved} color={Colors.statusResolved} />
      </View>

      {/* ── Filter Tabs ────────────────────────────────────────────── */}
      <View style={styles.filterContainer}>
        {FILTERS.map(f => (
          <TouchableOpacity
            key={f}
            style={[styles.filterTab, activeFilter === f && styles.filterTabActive]}
            onPress={() => handleFilterPress(f)}
            activeOpacity={0.7}>
            <Text
              style={[
                styles.filterTabText,
                activeFilter === f && styles.filterTabTextActive,
              ]}>
              {filterLabel(f)}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* ── List count ─────────────────────────────────────────────── */}
      <Text style={styles.listCount}>
        Showing {filtered.length} grievance{filtered.length !== 1 ? 's' : ''}
      </Text>
    </>
  );

  return (
    <View style={styles.flex}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.primary} />

      {/* ── Top Header ─────────────────────────────────────────────── */}
      <View style={styles.header}>
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          style={styles.backBtn}
          activeOpacity={0.7}>
          <Text style={styles.backIcon}>←</Text>
        </TouchableOpacity>
        <View style={styles.headerCenter}>
          <Text style={styles.headerTitle}>Grievances</Text>
          <Text style={styles.headerSubtitle}>All submitted complaints</Text>
        </View>
        <TouchableOpacity
          onPress={() => fetchComplaints()}
          style={styles.refreshBtn}
          activeOpacity={0.7}>
          <Text style={styles.refreshIcon}>↻</Text>
        </TouchableOpacity>
      </View>

      {/* ── Content ────────────────────────────────────────────────── */}
      {loading ? (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color={Colors.primary} />
          <Text style={styles.loadingText}>Loading grievances...</Text>
        </View>
      ) : error ? (
        <View style={styles.errorContainer}>
          <Text style={styles.errorIcon}>⚠️</Text>
          <Text style={styles.errorTitle}>Something went wrong</Text>
          <Text style={styles.errorMessage}>{error}</Text>
          <TouchableOpacity
            onPress={() => fetchComplaints()}
            style={styles.retryBtn}
            activeOpacity={0.8}>
            <Text style={styles.retryBtnText}>Try Again</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <FlatList
          data={filtered}
          keyExtractor={(item) => String(item.id)}
          renderItem={({item}) => <GrievanceCard item={item} />}
          ListHeaderComponent={renderHeader}
          ListEmptyComponent={renderEmpty}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={() => fetchComplaints(true)}
              colors={[Colors.primary]}
              tintColor={Colors.primary}
            />
          }
        />
      )}
    </View>
  );
};

/** Small stat chip component */
const StatChip = ({label, value, color}) => (
  <View style={[statStyles.chip, {borderTopColor: color}]}>
    <Text style={[statStyles.value, {color}]}>{value}</Text>
    <Text style={statStyles.label}>{label}</Text>
  </View>
);

const statStyles = StyleSheet.create({
  chip: {
    flex: 1,
    backgroundColor: Colors.background,
    borderRadius: 10,
    padding: 12,
    alignItems: 'center',
    borderTopWidth: 3,
    borderWidth: 1,
    borderColor: Colors.border,
    elevation: 1,
    shadowColor: Colors.shadow,
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.05,
    shadowRadius: 3,
  },
  value: {fontSize: 20, fontWeight: '800'},
  label: {fontSize: 10, color: Colors.textMuted, marginTop: 2, textAlign: 'center'},
});

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
    marginRight: 12,
  },
  backIcon: {fontSize: 18, color: Colors.textOnPrimary, fontWeight: '700'},
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
  refreshBtn: {
    width: 38,
    height: 38,
    borderRadius: 19,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 8,
  },
  refreshIcon: {fontSize: 20, color: Colors.textOnPrimary, fontWeight: '700'},

  // ── Stats Row ──────────────────────────────────────────────────────
  statsRow: {
    flexDirection: 'row',
    gap: 10,
    marginBottom: 16,
  },

  // ── Filter Tabs ───────────────────────────────────────────────────
  filterContainer: {
    flexDirection: 'row',
    backgroundColor: Colors.background,
    borderRadius: 10,
    padding: 4,
    borderWidth: 1,
    borderColor: Colors.border,
    marginBottom: 14,
    gap: 4,
  },
  filterTab: {
    flex: 1,
    paddingVertical: 8,
    borderRadius: 8,
    alignItems: 'center',
  },
  filterTabActive: {backgroundColor: Colors.primary},
  filterTabText: {
    fontSize: 11,
    fontWeight: '600',
    color: Colors.textSecondary,
    textAlign: 'center',
  },
  filterTabTextActive: {color: Colors.textOnPrimary},

  listCount: {
    fontSize: 12,
    color: Colors.textMuted,
    marginBottom: 10,
    fontWeight: '500',
  },

  // ── List ──────────────────────────────────────────────────────────
  list: {padding: 16, paddingBottom: 32},

  // ── Empty State ───────────────────────────────────────────────────
  emptyState: {
    alignItems: 'center',
    paddingVertical: 48,
    paddingHorizontal: 32,
  },
  emptyIcon: {fontSize: 48, marginBottom: 16},
  emptyTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: Colors.textPrimary,
    marginBottom: 8,
    textAlign: 'center',
  },
  emptySubtitle: {
    fontSize: 14,
    color: Colors.textSecondary,
    textAlign: 'center',
    lineHeight: 20,
  },

  // ── Loading ───────────────────────────────────────────────────────
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 14,
  },
  loadingText: {
    fontSize: 14,
    color: Colors.textSecondary,
    marginTop: 8,
  },

  // ── Error ─────────────────────────────────────────────────────────
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  errorIcon: {fontSize: 40, marginBottom: 12},
  errorTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: Colors.textPrimary,
    marginBottom: 8,
    textAlign: 'center',
  },
  errorMessage: {
    fontSize: 13,
    color: Colors.textSecondary,
    textAlign: 'center',
    marginBottom: 24,
    lineHeight: 20,
  },
  retryBtn: {
    backgroundColor: Colors.primary,
    paddingHorizontal: 28,
    paddingVertical: 12,
    borderRadius: 8,
  },
  retryBtnText: {
    color: Colors.textOnPrimary,
    fontWeight: '700',
    fontSize: 14,
  },
});

export default ViewGrievancesScreen;
