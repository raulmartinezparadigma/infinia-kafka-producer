import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(null);

    useEffect(() => {
        const storedToken = localStorage.getItem('admin_jwt');
        if (storedToken) {
            setToken(storedToken);
        }
    }, []);

    const login = async (username, password) => {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });

        if (!response.ok) {
            throw new Error('Invalid credentials');
        }

        const data = await response.json();
        localStorage.setItem('admin_jwt', data.token);
        setToken(data.token);
    };

    const logout = () => {
        localStorage.removeItem('admin_jwt');
        setToken(null);
    };

    return (
        <AuthContext.Provider value={{ token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
