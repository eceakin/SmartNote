import React, { useState, useEffect, createContext, useContext } from 'react';
import { Search, Plus, Edit, Trash2, User, LogOut, Save, X } from 'lucide-react';

// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Auth Context
const AuthContext = createContext();

const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// API Functions
const api = {
  // Auth endpoints
  login: async (credentials) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    if (!response.ok) throw new Error('Login failed');
    return response.json();
  },

  register: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
    if (!response.ok) throw new Error('Registration failed');
    return response.json();
  },

  // Notes endpoints
  getNotes: async (token) => {
    const response = await fetch(`${API_BASE_URL}/notes`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Failed to fetch notes');
    return response.json();
  },

  createNote: async (token, noteData) => {
    const response = await fetch(`${API_BASE_URL}/notes`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(noteData)
    });
    if (!response.ok) throw new Error('Failed to create note');
    return response.json();
  },

  updateNote: async (token, id, noteData) => {
    const response = await fetch(`${API_BASE_URL}/notes/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(noteData)
    });
    if (!response.ok) throw new Error('Failed to update note');
    return response.json();
  },

  deleteNote: async (token, id) => {
    const response = await fetch(`${API_BASE_URL}/notes/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Failed to delete note');
  },

  searchNotes: async (token, title) => {
    const response = await fetch(`${API_BASE_URL}/notes/search?title=${encodeURIComponent(title)}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Failed to search notes');
    return response.json();
  },

  // User endpoints
  getCurrentUser: async (token) => {
    const response = await fetch(`${API_BASE_URL}/users/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!response.ok) throw new Error('Failed to fetch user');
    return response.json();
  },

  updateUser: async (token, userData) => {
    const response = await fetch(`${API_BASE_URL}/users/me`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(userData)
    });
    if (!response.ok) throw new Error('Failed to update user');
    return response.json();
  }
};

// Auth Provider Component
const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.getCurrentUser(token)
        .then(userData => {
          setUser(userData);
        })
        .catch(() => {
          localStorage.removeItem('token');
          setToken(null);
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (credentials) => {
    const response = await api.login(credentials);
    setToken(response.token);
    setUser(response);
    localStorage.setItem('token', response.token);
    return response;
  };

  const register = async (userData) => {
    const response = await api.register(userData);
    setToken(response.token);
    setUser(response);
    localStorage.setItem('token', response.token);
    return response;
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
  };

  const value = {
    user,
    token,
    login,
    register,
    logout,
    isAuthenticated: !!token,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Login Component
const LoginForm = ({ onSwitchToRegister }) => {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(credentials);
    } catch (err) {
      setError('Giriş başarısız. Kullanıcı adı ve şifrenizi kontrol edin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: 'rgb(251, 243, 193)' }}>
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h2 className="text-2xl font-bold text-center mb-6" style={{ color: 'rgb(213, 11, 139)', fontFamily: 'Roboto, sans-serif' }}>
          Giriş Yap
        </h2>
        
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Kullanıcı Adı
            </label>
            <input
              type="text"
              value={credentials.username}
              onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Şifre
            </label>
            <input
              type="password"
              value={credentials.password}
              onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-lg text-white font-medium transition-colors"
            style={{ 
              backgroundColor: 'rgb(213, 11, 139)',
              fontFamily: 'Roboto, sans-serif'
            }}
          >
            {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
          </button>
        </form>
        
        <p className="text-center mt-4" style={{ fontFamily: 'Roboto, sans-serif' }}>
          Hesabınız yok mu?{' '}
          <button
            onClick={onSwitchToRegister}
            className="text-purple-600 hover:underline font-medium"
          >
            Kayıt Ol
          </button>
        </p>
      </div>
    </div>
  );
};

// Register Component
const RegisterForm = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { register } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await register(formData);
    } catch (err) {
      setError('Kayıt başarısız. Lütfen bilgilerinizi kontrol edin.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: 'rgb(251, 243, 193)' }}>
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h2 className="text-2xl font-bold text-center mb-6" style={{ color: 'rgb(213, 11, 139)', fontFamily: 'Roboto, sans-serif' }}>
          Kayıt Ol
        </h2>
        
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Kullanıcı Adı
            </label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              E-posta
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Ad
            </label>
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Soyad
            </label>
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Şifre
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>
          
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-lg text-white font-medium transition-colors"
            style={{ 
              backgroundColor: 'rgb(213, 11, 139)',
              fontFamily: 'Roboto, sans-serif'
            }}
          >
            {loading ? 'Kayıt olunuyor...' : 'Kayıt Ol'}
          </button>
        </form>
        
        <p className="text-center mt-4" style={{ fontFamily: 'Roboto, sans-serif' }}>
          Zaten hesabınız var mı?{' '}
          <button
            onClick={onSwitchToLogin}
            className="text-purple-600 hover:underline font-medium"
          >
            Giriş Yap
          </button>
        </p>
      </div>
    </div>
  );
};

// Note Card Component
const NoteCard = ({ note, onEdit, onDelete }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="flex justify-between items-start mb-3">
        <h3 className="text-xl font-semibold" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
          {note.title}
        </h3>
        <div className="flex space-x-2">
          <button
            onClick={() => onEdit(note)}
            className="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded"
          >
            <Edit size={18} />
          </button>
          <button
            onClick={() => onDelete(note.id)}
            className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded"
          >
            <Trash2 size={18} />
          </button>
        </div>
      </div>
      
      <p className="text-gray-700 mb-4" style={{ fontFamily: 'Roboto, sans-serif' }}>
        {note.description || 'Açıklama yok'}
      </p>
      
      <div className="text-sm text-gray-500" style={{ fontFamily: 'Roboto, sans-serif' }}>
        <p>Oluşturulma: {formatDate(note.createdAt)}</p>
        {note.updatedAt !== note.createdAt && (
          <p>Güncellenme: {formatDate(note.updatedAt)}</p>
        )}
      </div>
    </div>
  );
};

// Note Form Component
const NoteForm = ({ note, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    title: note?.title || '',
    description: note?.description || ''
  });
    const [loading, setLoading] = useState(false); // <<< burayı ekle


  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 mb-6">
      <h3 className="text-xl font-semibold mb-4" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
        {note ? 'Not Düzenle' : 'Yeni Not'}
      </h3>
      
      <div className="mb-4">
        <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
          Başlık
        </label>
        <input
          type="text"
          value={formData.title}
          onChange={(e) => setFormData({ ...formData, title: e.target.value })}
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
          style={{ fontFamily: 'Roboto, sans-serif' }}
          maxLength={100}
          required
        />
      </div>
      
      <div className="mb-4">
        <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
          Açıklama
        </label>
        <textarea
          value={formData.description}
          onChange={(e) => setFormData({ ...formData, description: e.target.value })}
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 h-32 resize-none"
          style={{ fontFamily: 'Roboto, sans-serif' }}
          maxLength={500}
        />
      </div>
      
      <div className="flex space-x-3">
        <button
          type="submit"
          disabled={loading || !formData.title.trim()}
          className="flex items-center px-4 py-2 rounded-lg text-white font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          style={{ 
            backgroundColor: loading || !formData.title.trim() ? '#76d3b5ff' : 'rgba(50, 185, 140, 1)', 
            fontFamily: 'Roboto, sans-serif' 
          }}
        >
          <Save size={18} className="mr-2" />
          {loading ? 'Kaydediliyor...' : 'Kaydet'}
        </button>
        <button
          type="button"
          onClick={onCancel}
          disabled={loading}
          className="flex items-center px-4 py-2 rounded-lg text-gray-700 bg-gray-200 font-medium transition-colors hover:bg-gray-300 disabled:opacity-50"
          style={{ fontFamily: 'Roboto, sans-serif' }}
        >
          <X size={18} className="mr-2" />
          İptal
        </button>
      </div>
    </form>
  );
};

// Profile Modal Component
const ProfileModal = ({ isOpen, onClose, user }) => {
  const [formData, setFormData] = useState({
    username: user?.username || '',
    email: user?.email || '',
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const { token } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      await api.updateUser(token, formData);
      setMessage('Profil başarıyla güncellendi!');
      setTimeout(() => {
        onClose();
        window.location.reload();
      }, 1500);
    } catch (err) {
      setMessage('Profil güncellenirken hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6 max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
            Profil Bilgileri
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            <X size={24} />
          </button>
        </div>

        {message && (
          <div className={`p-3 rounded mb-4 ${message.includes('başarıyla') ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Kullanıcı Adı
            </label>
            <input
              type="text"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              E-posta
            </label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Ad
            </label>
            <input
              type="text"
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Soyad
            </label>
            <input
              type="text"
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-2" style={{ fontFamily: 'Roboto, sans-serif' }}>
              Yeni Şifre (boş bırakılırsa değişmez)
            </label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
              style={{ fontFamily: 'Roboto, sans-serif' }}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-lg text-white font-medium transition-colors"
            style={{ 
              backgroundColor: 'rgba(60, 165, 130, 1)',
              fontFamily: 'Roboto, sans-serif'
            }}
          >
            {loading ? 'Güncelleniyor...' : 'Güncelle'}
          </button>
        </form>
      </div>
    </div>
  );
};

// Main Notes Dashboard Component
const NotesApp = () => {
  const [notes, setNotes] = useState([]);
  const [filteredNotes, setFilteredNotes] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [editingNote, setEditingNote] = useState(null);
  const [showNoteForm, setShowNoteForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showProfile, setShowProfile] = useState(false);
  const { user, token, logout } = useAuth();

  const loadNotes = async () => {
    try {
      const notesData = await api.getNotes(token);
      setNotes(notesData);
      setFilteredNotes(notesData);
    } catch (err) {
      console.error('Error loading notes:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotes();
  }, [token]);

  const handleSearch = async (term) => {
    setSearchTerm(term);
    if (term.trim() === '') {
      setFilteredNotes(notes);
    } else {
      try {
        const searchResults = await api.searchNotes(token, term);
        setFilteredNotes(searchResults);
      } catch (err) {
        console.error('Search error:', err);
        setFilteredNotes([]);
      }
    }
  };

  const handleSaveNote = async (noteData) => {
    try {
      if (editingNote) {
        await api.updateNote(token, editingNote.id, noteData);
      } else {
        await api.createNote(token, noteData);
      }
      await loadNotes();
      setShowNoteForm(false);
      setEditingNote(null);
    } catch (err) {
      console.error('Save error:', err);
    }
  };

  const handleDeleteNote = async (id) => {
    if (window.confirm('Bu notu silmek istediğinizden emin misiniz?')) {
      try {
        await api.deleteNote(token, id);
        await loadNotes();
      } catch (err) {
        console.error('Delete error:', err);
      }
    }
  };

  const handleEditNote = (note) => {
    setEditingNote(note);
    setShowNoteForm(true);
  };

  const handleCancelForm = () => {
    setShowNoteForm(false);
    setEditingNote(null);
  };

  return (
    <div className="min-h-screen" style={{ backgroundColor: 'rgb(251, 243, 193)' }}>
      {/* Header */}
      <header className="bg-white shadow-sm border-b" style={{ borderColor: 'rgb(220, 139, 224)' }}>
        <div className="max-w-6xl mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <h1 className="text-2xl font-bold" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
              Notlarım
            </h1>
            
            <div className="flex items-center space-x-4">
              <span className="text-gray-700" style={{ fontFamily: 'Roboto, sans-serif' }}>
                Hoş geldin, {user?.firstName} {user?.lastName}
              </span>
              <button
                onClick={() => setShowProfile(true)}
                className="p-2 text-gray-600 hover:text-purple-600 hover:bg-purple-50 rounded-lg transition-colors"
              >
                <User size={20} />
              </button>
              <button
                onClick={logout}
                className="p-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
              >
                <LogOut size={20} />
              </button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* Search and Add Note Section */}
        <div className="mb-8 space-y-4">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search Bar */}
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Notlarımda ara..."
                value={searchTerm}
                onChange={(e) => handleSearch(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                style={{ fontFamily: 'Roboto, sans-serif' }}
              />
            </div>
            
            {/* Add Note Button */}
            <button
              onClick={() => setShowNoteForm(true)}
              className="flex items-center px-6 py-3 rounded-lg text-white font-medium transition-colors hover:opacity-90"
              style={{ backgroundColor: 'rgba(217, 23, 227, 1)', fontFamily: 'Roboto, sans-serif' }}
            >
              <Plus size={20} className="mr-2" />
              Yeni Not
            </button>
          </div>
        </div>

        {/* Note Form */}
        {showNoteForm && (
          <NoteForm
            note={editingNote}
            onSave={handleSaveNote}
            onCancel={handleCancelForm}
          />
        )}

        {/* Notes Grid */}
        {loading ? (
          <div className="text-center py-12">
            <div className="text-xl" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
              Notlar yükleniyor...
            </div>
          </div>
        ) : filteredNotes.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-xl mb-4" style={{ fontFamily: 'Roboto, sans-serif', color: 'rgb(213, 11, 139)' }}>
              {searchTerm ? 'Arama kriterlerinize uygun not bulunamadı' : 'Henüz notunuz yok'}
            </div>
            {!searchTerm && (
              <button
                onClick={() => setShowNoteForm(true)}
                className="px-6 py-3 rounded-lg text-white font-medium transition-colors"
                style={{ backgroundColor: 'rgba(217, 23, 227, 1)', fontFamily: 'Roboto, sans-serif' }}
              >
                İlk notunuzu ekleyin
              </button>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredNotes.map((note) => (
              <NoteCard
                key={note.id}
                note={note}
                onEdit={handleEditNote}
                onDelete={handleDeleteNote}
              />
            ))}
          </div>
        )}
      </div>

      {/* Profile Modal */}
      <ProfileModal
        isOpen={showProfile}
        onClose={() => setShowProfile(false)}
        user={user}
      />
    </div>
  );
};

// Auth Component
const AuthComponent = () => {
  const [isLogin, setIsLogin] = useState(true);

  return isLogin ? (
    <LoginForm onSwitchToRegister={() => setIsLogin(false)} />
  ) : (
    <RegisterForm onSwitchToLogin={() => setIsLogin(true)} />
  );
};

// Main App Component
const App = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

const AppContent = () => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: 'rgb(251, 243, 193)' }}>
        <div className="text-2xl font-bold" style={{ color: 'rgb(213, 11, 139)', fontFamily: 'Roboto, sans-serif' }}>
          Yükleniyor...
        </div>
      </div>
    );
  }

  return isAuthenticated ? <NotesApp /> : <AuthComponent />;
};

export default App;