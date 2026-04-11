import { api } from './api';
import { Task, TaskStatus, TaskPriority } from '../types';

export const taskService = {
  getTasks: async (projectId: string, filters?: { status?: string; assignee?: string }): Promise<Task[]> => {
    const params = new URLSearchParams();
    if (filters?.status) params.append('status', filters.status);
    if (filters?.assignee) params.append('assignee', filters.assignee);
    
    const queryString = params.toString() ? `?${params.toString()}` : '';
    const { data } = await api.get<Task[]>(`/projects/${projectId}/tasks${queryString}`);
    return data;
  },

  createTask: async (projectId: string, payload: {
    title: string;
    description?: string;
    priority: TaskPriority;
    assigneeId?: string;
    dueDate?: string;
    storyPoints?: number;
  }): Promise<Task> => {
    const { data } = await api.post<Task>(`/projects/${projectId}/tasks`, payload);
    return data;
  },

  updateTask: async (taskId: string, payload: {
    title?: string;
    description?: string;
    status?: TaskStatus;
    priority?: TaskPriority;
    assigneeId?: string;
    dueDate?: string;
    storyPoints?: number;
  }): Promise<Task> => {
    const { data } = await api.patch<Task>(`/tasks/${taskId}`, payload);
    return data;
  },

  deleteTask: async (taskId: string): Promise<void> => {
    await api.delete(`/tasks/${taskId}`);
  }
};
