import React, { useContext } from 'react';
import AdminKafkaPanel from './AdminKafkaPanel';
import LoginPage from './LoginPage';
import { AuthProvider, AuthContext } from './AuthContext';
import { CssBaseline, Container } from '@mui/material';

function AppContent() {
  const { token } = useContext(AuthContext);
  return token ? <AdminKafkaPanel /> : <LoginPage />;
}

function App() {
  return (
    <AuthProvider>
      <CssBaseline />
      <Container>
        <AppContent />
      </Container>
    </AuthProvider>
  );
}

export default App;
