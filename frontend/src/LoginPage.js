import React, { useState, useContext } from 'react';
import { Box, Typography, TextField, Button, Paper, Alert } from '@mui/material';
import { AuthContext } from './AuthContext';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const { login } = useContext(AuthContext);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await login(username, password);
        } catch (err) {
            setError(err.message || 'Failed to login. Please check your credentials.');
        }
    };

    return (
        <Box sx={{ maxWidth: 400, m: '100px auto', p: 3 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Typography variant="h4" gutterBottom align="center">
                    Admin Login
                </Typography>
                <form onSubmit={handleLogin}>
                    <TextField
                        label="Username"
                        fullWidth
                        margin="normal"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <TextField
                        label="Password"
                        type="password"
                        fullWidth
                        margin="normal"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
                    <Button type="submit" variant="contained" color="primary" fullWidth sx={{ mt: 3 }}>
                        Login
                    </Button>
                </form>
            </Paper>
        </Box>
    );
};

export default LoginPage;
