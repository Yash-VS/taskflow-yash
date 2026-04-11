import { api } from './api';
import { UserInfo } from '../types';

export const userService = {
  getUsers: async (): Promise<UserInfo[]> => {
    const { data } = await api.get<UserInfo[]>('/users');
    return data;
  }
};
