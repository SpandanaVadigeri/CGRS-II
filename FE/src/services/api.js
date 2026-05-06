import axios from 'axios';
import { getToken, removeToken } from '../storage/authStorage';

/**
 * Base Axios instance pointing to the Spring Boot backend.
 * All authenticated requests automatically include the JWT token.
 */
const BASE_URL = 'http://10.0.2.2:8080'; // Android emulator → localhost

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Request Interceptor — attach token ───────────────────────────────────────
api.interceptors.request.use(
  async (config) => {
    const token = await getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// ─── Response Interceptor — handle auth errors ────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response) {
      const {status} = error.response;
      if (status === 401) {
        // Token expired or invalid — clear local storage
        await removeToken();
        // Caller will handle navigation
      }
      if (status === 403) {
        console.warn('Access forbidden — insufficient role');
      }
    }
    return Promise.reject(error);
  },
);

// ─── Auth APIs ────────────────────────────────────────────────────────────────

/**
 * POST /auth/login
 * Returns: { token, email, role }
 */
export const loginUser = (email, password) =>
  api.post('/auth/login', {email, password});

/**
 * POST /auth/register
 * Returns: { message, name, email, role }  — NO token returned
 */
export const registerUser = (name, email, password, role) =>
  api.post('/auth/register', {name, email, password, role});

// ─── Complaint APIs ───────────────────────────────────────────────────────────

/**
 * GET /complaints
 * Returns array of all complaints (requires JWT).
 */
export const getAllComplaints = () => api.get('/complaints');

/**
 * GET /complaints/:id
 * Returns a single complaint by ID.
 */
export const getComplaintById = (id) => api.get(`/complaints/${id}`);

/**
 * POST /complaints
 * Create a new complaint.
 * Body: { title, description, category }
 */
export const createComplaint = (title, description, category) =>
  api.post('/complaints', {title, description, category});

/**
 * PUT /complaints/:id
 * Update an existing complaint.
 */
export const updateComplaint = (id, data) => api.put(`/complaints/${id}`, data);

/**
 * DELETE /complaints/:id
 */
export const deleteComplaint = (id) => api.delete(`/complaints/${id}`);

export default api;
