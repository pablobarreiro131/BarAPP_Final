const BASE_URL = 'http://localhost:8081/api';

export const apiClient = {
  async fetchWithAuth(endpoint, options = {}) {
    let token = localStorage.getItem('supabase_token');

    if (!token) {
      try {
        const { supabase } = await import('./supabaseClient');
        const { data: { session } } = await supabase.auth.getSession();
        token = session?.access_token;
      } catch (e) {
      }
    }

    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Error desconocido' }));
      throw new Error(error.message || `Error ${response.status}`);
    }

    return response.json().catch(() => null);
  },

  getMe: () => apiClient.fetchWithAuth('/perfiles/me'),
  getPerfiles: () => apiClient.fetchWithAuth('/perfiles'),
  createPerfil: (perfil) => apiClient.fetchWithAuth('/perfiles', {
    method: 'POST',
    body: JSON.stringify(perfil)
  }),

  getMesas: () => apiClient.fetchWithAuth('/mesas'),
  createMesa: (mesa) => apiClient.fetchWithAuth('/mesas', {
    method: 'POST',
    body: JSON.stringify(mesa)
  }),
  deleteMesa: (id) => apiClient.fetchWithAuth(`/mesas/${id}`, {
    method: 'DELETE'
  }),

  getCategorias: () => apiClient.fetchWithAuth('/categorias'),
  createCategoria: (categoria) => apiClient.fetchWithAuth('/categorias', {
    method: 'POST',
    body: JSON.stringify(categoria)
  }),
  getProductos: () => apiClient.fetchWithAuth('/productos'),
  createProducto: (producto) => apiClient.fetchWithAuth('/productos', {
    method: 'POST',
    body: JSON.stringify(producto)
  }),

  getDashboardResumen: () => apiClient.fetchWithAuth('/dashboard/resumen'),
};
