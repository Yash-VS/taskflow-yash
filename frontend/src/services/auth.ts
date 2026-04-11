import { api } from './api';
import { AuthResponse } from '../types';

export const authService = {
  login: async (credentials: Record<string, string>): Promise<AuthResponse> => {
    const { data } = await api.post<AuthResponse>('/auth/login', credentials);
    return data;
  },

  register: async (credentials: Record<string, string>): Promise<AuthResponse> => {
    const { data } = await api.post<AuthResponse>('/auth/register', credentials);
    return data;
  }
};
