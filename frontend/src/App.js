import React from 'react';
import AdminKafkaPanel from './AdminKafkaPanel';
import { CssBaseline, Container } from '@mui/material';

function App() {
  return (
    <>
      <CssBaseline />
      <Container>
        <AdminKafkaPanel />
      </Container>
    </>
  );
}

export default App;
