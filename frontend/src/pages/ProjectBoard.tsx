import React, { useState, useMemo } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { DragDropContext, Droppable, Draggable, DropResult } from '@hello-pangea/dnd';
import { projectService } from '../services/project';
import { taskService } from '../services/task';
import { userService } from '../services/user';
import { Task, TaskStatus, TaskPriority } from '../types';
import { Navbar } from '../components/Navbar';
import { ArrowLeft, Plus, Search, User as UserIcon, Calendar, Hash } from 'lucide-react';
import './ProjectBoard.css';

const COLUMNS: { id: TaskStatus; title: string }[] = [
  { id: 'todo', title: 'To Do' },
  { id: 'in_progress', title: 'In Progress' },
  { id: 'done', title: 'Done' }
];

export const ProjectBoard: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const queryClient = useQueryClient();
  
  // Modal states
  const [isCreateTaskOpen, setIsCreateTaskOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);

  // Form states (reused for create/edit)
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState<TaskPriority>('medium');
  const [assigneeId, setAssigneeId] = useState<string>('');
  const [dueDate, setDueDate] = useState('');
  const [storyPoints, setStoryPoints] = useState<number | undefined>(undefined);
  
  // Search State for Assignee
  const [userSearch, setUserSearch] = useState('');
  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);

  const { data: project, isLoading } = useQuery({
    queryKey: ['project', id],
    queryFn: () => projectService.getProject(id!)
  });

  const { data: stats } = useQuery({
    queryKey: ['projectStats', id],
    queryFn: () => projectService.getProjectStats(id!)
  });

  const { data: users } = useQuery({
    queryKey: ['users'],
    queryFn: userService.getUsers,
    enabled: isCreateTaskOpen || isEditModalOpen
  });

  const tasks = project?.tasks || [];

  const filteredUsers = useMemo(() => {
    if (!users) return [];
    return users.filter(u => 
      u.name.toLowerCase().includes(userSearch.toLowerCase()) || 
      u.email.toLowerCase().includes(userSearch.toLowerCase())
    );
  }, [users, userSearch]);

  const selectedUserName = useMemo(() => {
    return users?.find(u => u.id === (assigneeId))?.name || 'Select Assignee';
  }, [users, assigneeId]);

  const tasksByStatus = useMemo(() => {
    const grouped: Record<TaskStatus, Task[]> = {
      todo: [],
      in_progress: [],
      done: []
    };
    tasks.forEach(task => {
      if (grouped[task.status]) {
        grouped[task.status].push(task);
      } else {
        grouped.todo.push(task); 
      }
    });
    return grouped;
  }, [tasks]);

  const updateTaskMutation = useMutation({
    mutationFn: ({ taskId, payload }: { taskId: string; payload: any }) => 
      taskService.updateTask(taskId, payload),
    onMutate: async ({ taskId, payload }) => {
      await queryClient.cancelQueries({ queryKey: ['project', id] });
      const previousProject = queryClient.getQueryData(['project', id]);
      
      queryClient.setQueryData(['project', id], (old: any) => {
        if (!old) return old;
        return {
          ...old,
          tasks: old.tasks.map((t: Task) => 
            t.id === taskId ? { ...t, ...payload } : t
          )
        };
      });

      return { previousProject };
    },
    onError: (_err, _variables, context) => {
      if (context?.previousProject) {
        queryClient.setQueryData(['project', id], context.previousProject);
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['project', id] });
    },
    onSuccess: () => {
      closeModals();
    }
  });

  const createTaskMutation = useMutation({
    mutationFn: (payload: any) =>
      taskService.createTask(id!, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['project', id] });
      closeModals();
    }
  });

  const deleteTaskMutation = useMutation({
    mutationFn: (taskId: string) => taskService.deleteTask(taskId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['project', id] });
      closeModals();
    }
  });

  const openCreateModal = () => {
    resetForm();
    setIsCreateTaskOpen(true);
  };

  const openEditModal = (task: Task) => {
    setSelectedTask(task);
    setTitle(task.title);
    setDescription(task.description || '');
    setPriority(task.priority);
    setAssigneeId(task.assigneeId || '');
    setDueDate(task.dueDate || '');
    setStoryPoints(task.storyPoints);
    setIsEditModalOpen(true);
  };

  const closeModals = () => {
    setIsCreateTaskOpen(false);
    setIsEditModalOpen(false);
    setSelectedTask(null);
    resetForm();
  };

  const resetForm = () => {
    setTitle('');
    setDescription('');
    setPriority('medium');
    setAssigneeId('');
    setDueDate('');
    setStoryPoints(undefined);
    setUserSearch('');
    setIsUserDropdownOpen(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;
    
    const payload: any = { 
      title, 
      description,
      priority,
      assigneeId: assigneeId || undefined,
      dueDate: dueDate || undefined,
      storyPoints
    };

    if (isEditModalOpen && selectedTask) {
      updateTaskMutation.mutate({ taskId: selectedTask.id, payload });
    } else {
      payload.status = 'todo'; // Add required status for new tasks
      createTaskMutation.mutate(payload);
    }
  };

  const onDragEnd = (result: DropResult) => {
    const { destination, source, draggableId } = result;
    if (!destination) return;
    if (destination.droppableId === source.droppableId && destination.index === source.index) return;

    const newStatus = destination.droppableId as TaskStatus;
    updateTaskMutation.mutate({ taskId: draggableId, payload: { status: newStatus } });
  };

  if (isLoading) return <><Navbar /><div className="loading-state">Loading board...</div></>;
  if (!project) return <><Navbar /><div className="error-state">Project not found</div></>;

  return (
    <>
      <Navbar />
      <main className="container board-page">
        <div className="board-header">
          <div className="board-header-left">
            <Link to="/projects" className="btn-secondary mb-4" style={{display: 'inline-flex', padding: '0.2rem 0.5rem'}}>
              <ArrowLeft size={16} /> Back
            </Link>
            <h1>{project.name}</h1>
            <p className="subtitle">{project.description}</p>
          </div>
          <div className="board-meta">
             <div className="meta-item">
                <UserIcon size={14} /> <span>Owner: {project.ownerName || 'You'}</span>
             </div>
             <div className="meta-item">
                <Calendar size={14} /> <span>Created: {new Date(project.createdAt).toLocaleDateString()}</span>
             </div>
          </div>
          
          {stats && (
            <div className="project-stats-container">
              <div className="stat-group">
                <h4>By Status</h4>
                <div className="stat-badges">
                  {Object.entries(stats.tasksByStatus).map(([status, count]) => (
                    <span key={status} className={`stat-badge status-${status.toLowerCase()}`}>
                      {status}: {count}
                    </span>
                  ))}
                </div>
              </div>
              <div className="stat-group">
                <h4>By Assignee</h4>
                <div className="stat-badges">
                  {Object.entries(stats.tasksByAssignee).map(([assignee, count]) => (
                    <span key={assignee} className="stat-badge user-badge">
                      <UserIcon size={12} /> {assignee}: {count}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          )}

          <div className="board-filters">
            <button className="btn-primary" onClick={openCreateModal}>
              <Plus size={18} /> Add Task
            </button>
          </div>
        </div>

        <DragDropContext onDragEnd={onDragEnd}>
          <div className="kanban-board">
            {COLUMNS.map(column => (
              <div key={column.id} className={`kanban-column column-${column.id}`}>
                <div className="kanban-column-header">
                  <span>{column.title}</span>
                  <span className="column-count">{tasksByStatus[column.id].length}</span>
                </div>
                
                <Droppable droppableId={column.id}>
                  {(provided, snapshot) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                      className={`kanban-droppable ${snapshot.isDraggingOver ? 'is-dragging-over' : ''}`}
                    >
                      {tasksByStatus[column.id].map((task, index) => (
                        <Draggable key={task.id} draggableId={task.id} index={index}>
                          {(provided, snapshot) => (
                            <div
                              ref={provided.innerRef}
                              {...provided.draggableProps}
                              {...provided.dragHandleProps}
                              className={`task-card status-${task.status} priority-border-${task.priority} ${snapshot.isDragging ? 'is-dragging' : ''}`}
                              onClick={() => openEditModal(task)}
                            >
                              <div className="task-card-header">
                                <div className="task-title">{task.title}</div>
                              </div>
                              {task.description && <div className="task-card-desc">{task.description}</div>}
                              
                              <div className="task-meta">
                                <div className="left-meta">
                                  <span className={`priority-badge priority-${task.priority}`}>
                                    {task.priority}
                                  </span>
                                  {task.storyPoints !== undefined && (
                                    <span className="points-badge">
                                       <Hash size={12} /> {task.storyPoints}
                                    </span>
                                  )}
                                </div>
                                <div className="right-meta">
                                   {task.assigneeName && (
                                      <div className="assignee-tag" title={`Assigned to ${task.assigneeName}`}>
                                         <UserIcon size={12} /> {task.assigneeName.split(' ')[0]}
                                      </div>
                                   )}
                                  {task.dueDate && (
                                    <span className="due-date-badge">
                                      <Calendar size={12} /> {new Date(task.dueDate).toLocaleDateString(undefined, {month: 'short', day: 'numeric'})}
                                    </span>
                                  )}
                                </div>
                              </div>
                            </div>
                          )}
                        </Draggable>
                      ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </div>
            ))}
          </div>
        </DragDropContext>
      </main>

      {(isCreateTaskOpen || isEditModalOpen) && (
        <div className="modal-overlay" onClick={closeModals}>
          <div className="modal-content large-modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{isEditModalOpen ? 'Task Details' : `New Task for ${project.name}`}</h2>
              <button className="close-btn" onClick={closeModals}>&times;</button>
            </div>
            <form onSubmit={handleSubmit} className="modal-body">
              <div className="form-grid">
                <div className="form-main">
                  <div className="form-group mb-4">
                    <label className="label">Title <span className="text-danger">*</span></label>
                    <input 
                      type="text" 
                      className="input-field" 
                      autoFocus
                      required
                      value={title}
                      onChange={e => setTitle(e.target.value)}
                      placeholder="What needs to be done?"
                    />
                  </div>
                  <div className="form-group mb-4">
                    <label className="label">Description <span className="text-danger">*</span></label>
                    <textarea 
                      className="input-field" 
                      rows={8}
                      required
                      value={description}
                      onChange={e => setDescription(e.target.value)}
                      placeholder="Detailed explanation..."
                    />
                  </div>
                </div>

                <div className="form-sidebar">
                   <div className="form-group mb-4">
                    <label className="label">Priority <span className="text-danger">*</span></label>
                    <select 
                      className="input-field" 
                      required
                      value={priority}
                      onChange={e => setPriority(e.target.value as TaskPriority)}
                    >
                      <option value="low">Low</option>
                      <option value="medium">Medium</option>
                      <option value="high">High</option>
                    </select>
                  </div>

                  <div className="form-group mb-4 dropdown-container">
                    <label className="label">Assignee <span className="text-danger">*</span></label>
                    <div className={`searchable-dropdown ${!assigneeId ? 'required-error' : ''}`}>
                      <div className="dropdown-trigger" onClick={() => setIsUserDropdownOpen(!isUserDropdownOpen)}>
                         <UserIcon size={14} /> {selectedUserName}
                      </div>
                      
                      {isUserDropdownOpen && (
                        <div className="dropdown-menu">
                          <div className="dropdown-search">
                            <Search size={14} />
                            <input 
                              type="text" 
                              placeholder="Search users..." 
                              value={userSearch}
                              onChange={e => setUserSearch(e.target.value)}
                              onClick={e => e.stopPropagation()}
                            />
                          </div>
                          <div className="dropdown-list">
                            <div className="dropdown-item" onClick={() => { setAssigneeId(''); setIsUserDropdownOpen(false); }}>
                               Select Assignee
                            </div>
                            {filteredUsers.map(u => (
                              <div key={u.id} className="dropdown-item" onClick={() => { setAssigneeId(u.id); setIsUserDropdownOpen(false); }}>
                                 {u.name} <small>({u.email})</small>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="form-group mb-4">
                    <label className="label">Due Date <span className="text-danger">*</span></label>
                    <input 
                      type="date" 
                      className="input-field"
                      required
                      value={dueDate}
                      onChange={e => setDueDate(e.target.value)}
                    />
                  </div>

                  <div className="form-group mb-4">
                    <label className="label">Story Points <span className="text-danger">*</span></label>
                    <input 
                      type="number" 
                      className="input-field"
                      min="0"
                      required
                      value={storyPoints === undefined ? '' : storyPoints}
                      onChange={e => setStoryPoints(e.target.value ? parseInt(e.target.value) : undefined)}
                      placeholder="0"
                    />
                  </div>
                  
                  {isEditModalOpen && (
                    <div className="task-history-mini">
                       <p>Created: {new Date(selectedTask!.createdAt).toLocaleString()}</p>
                       <p>Updated: {new Date(selectedTask!.updatedAt).toLocaleString()}</p>
                    </div>
                  )}
                </div>
              </div>
              
              <div className="modal-actions mt-6 flex justify-between">
                <div>
                   {isEditModalOpen && (
                     <button 
                       type="button" 
                       className="btn-danger" 
                       onClick={() => { if(confirm('Delete this task?')) deleteTaskMutation.mutate(selectedTask!.id) }}
                       disabled={deleteTaskMutation.isPending}
                     >
                       {deleteTaskMutation.isPending ? 'Deleting...' : 'Delete Task'}
                     </button>
                   )}
                </div>
                <div className="flex gap-2">
                  <button type="button" className="btn-secondary" onClick={closeModals}>Cancel</button>
                  <button 
                    type="submit" 
                    className="btn-primary" 
                    disabled={
                      createTaskMutation.isPending || 
                      updateTaskMutation.isPending || 
                      !title.trim() || 
                      !description.trim() || 
                      !assigneeId || 
                      !dueDate || 
                      storyPoints === undefined
                    }
                  >
                    {isEditModalOpen 
                      ? (updateTaskMutation.isPending ? 'Saving...' : 'Save Changes')
                      : (createTaskMutation.isPending ? 'Creating...' : 'Create Task')
                    }
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};
