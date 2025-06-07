// Función para manejar el token JWT
const TokenManager = {
    setToken: function(token) {
        localStorage.setItem('jwt_token', token);
    },
    
    getToken: function() {
        return localStorage.getItem('jwt_token');
    },
    
    removeToken: function() {
        localStorage.removeItem('jwt_token');
    },
    
    isAuthenticated: function() {
        return this.getToken() !== null;
    }
};

// Función para manejar el rol del usuario
const UserManager = {
    setUser: function(user) {
        localStorage.setItem('user', JSON.stringify(user));
    },
    
    getUser: function() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },
    
    removeUser: function() {
        localStorage.removeItem('user');
    },
    
    getRole: function() {
        const user = this.getUser();
        return user ? user.role : null;
    },
    
    isAdmin: function() {
        return this.getRole() === 'ADMIN';
    },
    
    isLibrarian: function() {
        return this.getRole() === 'LIBRARIAN';
    },
    
    isReader: function() {
        return this.getRole() === 'READER';
    }
};

// Función para mostrar mensajes de alerta
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;
    
    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);
    
    // Eliminar la alerta después de 5 segundos
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Función para realizar peticiones HTTP con el token JWT
async function fetchWithAuth(url, options = {}) {
    // Obtener el token
    const token = TokenManager.getToken();
    
    // Si hay token, agregarlo a los headers
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        };
    }
    
    try {
        const response = await fetch(url, options);
        
        // Si la respuesta es 401 (Unauthorized), redirigir al login
        if (response.status === 401) {
            TokenManager.removeToken();
            UserManager.removeUser();
            window.location.href = '/auth/login';
            return null;
        }
        
        return response;
    } catch (error) {
        console.error('Error en la petición:', error);
        throw error;
    }
}

// Función para iniciar sesión
async function login(email, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            TokenManager.setToken(data.token);
            UserManager.setUser(data.user);
            
            // Redirigir según el rol
            if (UserManager.isAdmin()) {
                window.location.href = '/admin/dashboard';
            } else if (UserManager.isLibrarian()) {
                window.location.href = '/librarian/dashboard';
            } else {
                window.location.href = '/dashboard';
            }
            
            return true;
        } else {
            const error = await response.json();
            showAlert(error.message || 'Credenciales inválidas', 'danger');
            return false;
        }
    } catch (error) {
        console.error('Error en el inicio de sesión:', error);
        showAlert('Error de conexión con el servidor', 'danger');
        return false;
    }
}

// Función para registrarse
async function register(name, email, password) {
    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            TokenManager.setToken(data.token);
            UserManager.setUser(data.user);
            window.location.href = '/dashboard';
            return true;
        } else {
            const error = await response.json();
            showAlert(error.message || 'Error en el registro', 'danger');
            return false;
        }
    } catch (error) {
        console.error('Error en el registro:', error);
        showAlert('Error de conexión con el servidor', 'danger');
        return false;
    }
}

// Función para cerrar sesión
function logout() {
    TokenManager.removeToken();
    UserManager.removeUser();
    window.location.href = '/login';
}

// Inicialización cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    // Verificar autenticación en páginas protegidas
    const protectedPages = document.querySelector('.protected-page');
    if (protectedPages && !TokenManager.isAuthenticated()) {
        window.location.href = '/login';
    }
    
    // Formulario de inicio de sesión
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            login(email, password);
        });
    }
    
    // Formulario de registro
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            register(name, email, password);
        });
    }
    
    // Botón de logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(event) {
            event.preventDefault();
            logout();
        });
    }
    
    // Mostrar/ocultar elementos según el rol
    const adminElements = document.querySelectorAll('.admin-only');
    const librarianElements = document.querySelectorAll('.librarian-only');
    const readerElements = document.querySelectorAll('.reader-only');
    
    if (UserManager.isAdmin()) {
        adminElements.forEach(el => el.style.display = 'block');
        librarianElements.forEach(el => el.style.display = 'block');
    } else if (UserManager.isLibrarian()) {
        adminElements.forEach(el => el.style.display = 'none');
        librarianElements.forEach(el => el.style.display = 'block');
    } else {
        adminElements.forEach(el => el.style.display = 'none');
        librarianElements.forEach(el => el.style.display = 'none');
    }
    
    if (UserManager.isReader()) {
        readerElements.forEach(el => el.style.display = 'block');
    } else {
        readerElements.forEach(el => el.style.display = 'none');
    }
});
