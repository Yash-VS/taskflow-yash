import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { projectService } from '../services/project';
import { Plus, FolderGit2 } from 'lucide-react';
import { Navbar } from '../components/Navbar';
import './ProjectList.css';

export const ProjectList: React.FC = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newProjectName, setNewProjectName] = useState('');
  const [newProjectDesc, setNewProjectDesc] = useState('');
  
  const queryClient = useQueryClient();

  const { data: projects, isLoading, error } = useQuery({
    queryKey: ['projects'],
    queryFn: projectService.getProjects
  });

  const createMutation = useMutation({
    mutationFn: projectService.createProject,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      setIsModalOpen(false);
      setNewProjectName('');
      setNewProjectDesc('');
    }
  });

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newProjectName.trim()) return;
    createMutation.mutate({ name: newProjectName, description: newProjectDesc });
  };

  return (
    <>
      <Navbar />
      <main className="container page-wrapper">
        <div className="page-header">
          <div>
            <h1>Your Projects</h1>
            <p className="subtitle">Manage workspaces and collaborate across your team.</p>
          </div>
          <button className="btn-primary" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} /> New Project
          </button>
        </div>

        {isLoading ? (
          <div className="loading-state">Loading projects...</div>
        ) : error ? (
          <div className="error-state">Failed to load projects.</div>
        ) : projects?.length === 0 ? (
          <div className="empty-state">
            <FolderGit2 size={48} color="var(--color-text-muted)" />
            <h2>No Projects Yet</h2>
            <p>Create your first project to start managing tasks.</p>
            <button className="btn-primary mt-4" onClick={() => setIsModalOpen(true)}>
              <Plus size={18} /> Create Project
            </button>
          </div>
        ) : (
          <div className="project-grid">
            {projects?.map(project => (
              <Link to={`/projects/${project.id}`} key={project.id} className="project-card card">
                <div className="project-card-header">
                  <FolderGit2 size={24} color="var(--color-primary)" />
                  <h3>{project.name}</h3>
                </div>
                <p className="project-desc">
                  {project.description || 'No description provided.'}
                </p>
                <div className="project-footer">
                  <span className="date-badge">Created {new Date(project.createdAt).toLocaleDateString()}</span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </main>

      {/* Inline Create Modal for simplicity */}
      {isModalOpen && (
        <div className="modal-overlay" onClick={() => setIsModalOpen(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Create New Project</h2>
              <button className="close-btn" onClick={() => setIsModalOpen(false)}>&times;</button>
            </div>
            <form onSubmit={handleCreate} className="modal-body">
              <div className="form-group mb-4">
                <label className="label">Project Name</label>
                <input 
                  type="text" 
                  className="input-field" 
                  autoFocus
                  required
                  value={newProjectName}
                  onChange={e => setNewProjectName(e.target.value)}
                  placeholder="E.g. Website Redesign"
                />
              </div>
              <div className="form-group mb-4">
                <label className="label">Description (Optional)</label>
                <textarea 
                  className="input-field" 
                  rows={3}
                  value={newProjectDesc}
                  onChange={e => setNewProjectDesc(e.target.value)}
                  placeholder="What is this project about?"
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={createMutation.isPending || !newProjectName.trim()}>
                  {createMutation.isPending ? 'Creating...' : 'Create Project'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};
