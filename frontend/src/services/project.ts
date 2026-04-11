import { api } from './api';
import { Project } from '../types';

export const projectService = {
  getProjects: async (): Promise<Project[]> => {
    const { data } = await api.get<Project[]>('/projects');
    return data;
  },

  getProject: async (id: string): Promise<Project> => {
    const { data } = await api.get<Project>(`/projects/${id}`);
    return data;
  },

  createProject: async (payload: { name: string; description?: string }): Promise<Project> => {
    const { data } = await api.post<Project>('/projects', payload);
    return data;
  },

  updateProject: async (id: string, payload: { name?: string; description?: string }): Promise<Project> => {
    const { data } = await api.patch<Project>(`/projects/${id}`, payload);
    return data;
  },

  deleteProject: async (id: string): Promise<void> => {
    await api.delete(`/projects/${id}`);
  },

  getProjectStats: async (id: string): Promise<{ tasksByStatus: Record<string, number>, tasksByAssignee: Record<string, number> }> => {
    const { data } = await api.get(`/projects/${id}/stats`);
    return data;
  }
};
