import React, { useState, useContext } from 'react';
import { Box, Typography, TextField, Button, Paper, Alert, CircularProgress } from '@mui/material';
import { AuthContext } from './AuthContext';

function generarUUID() {
  // RFC4122 version 4 compliant
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c === 'x' ? r : ((r & 0x3) | 0x8);
    return v.toString(16);
  });
}

function plantillaProducto(uuid) {
  return `{
  "id": "${uuid}",
  "skuId": "123456789012345678",
  "type": "SNEAKERS", 
  "description": "Zapatillas deportivas de ejemplo",
  "price": 59.99,
  "size": "42",
  "imageUrl": "zapatillas_ejemplo.jpg"
}`;
}

const AdminKafkaPanel = () => {
  const [jsonInput, setJsonInput] = useState(plantillaProducto(generarUUID()));
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [imageGenLoading, setImageGenLoading] = useState(false);
  const [generatedImageUrl, setGeneratedImageUrl] = useState(null);
  const [imageGenError, setImageGenError] = useState(null);
  const { token, logout } = useContext(AuthContext);

  const handleGenerateImage = async () => {
    setImageGenLoading(true);
    setGeneratedImageUrl(null);
    setImageGenError(null);

    try {
      const parsed = JSON.parse(jsonInput);
      const description = parsed.description;

      if (!description) {
        throw new Error('El campo "description" del JSON no puede estar vacío.');
      }

      const response = await fetch('/api/ai/generate-image', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify({ description }),
      });

      if (!response.ok) {
        const err = await response.json();
        throw new Error(err.message || 'Error en el servidor al generar la imagen.');
      }

      const result = await response.json();

      if (result.success && result.imageUrl) {
        setGeneratedImageUrl(result.imageUrl);
        // Actualizar el JSON en el editor
        const updatedParsed = { ...parsed, imageUrl: result.imageUrl };
        setJsonInput(JSON.stringify(updatedParsed, null, 2));
      } else {
        throw new Error('La API no devolvió una imagen válida.');
      }

    } catch (e) {
      setImageGenError('Error al generar la imagen: ' + e.message);
    } finally {
      setImageGenLoading(false);
    }
  };

  const handleSendToKafka = async () => {
    setResult(null);
    setError(null);
    try {
      const parsed = JSON.parse(jsonInput);
      const response = await fetch('/api/admin/kafka/product', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(parsed),
      });
      if (!response.ok) {
        const err = await response.text();
        throw new Error(err);
      }
      setResult('Producto enviado correctamente a la cola Kafka.');
    } catch (e) {
      setError('Error al enviar el producto: ' + e.message);
    }
  };

  return (
    <Box sx={{ maxWidth: 600, m: '40px auto', p: 3 }}>
      <Paper elevation={3} sx={{ p: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h5" gutterBottom>
            Alta manual de producto en Kafka
          </Typography>
          <Button variant="outlined" color="secondary" onClick={logout}>
            Cerrar Sesión
          </Button>
        </Box>
        <Typography variant="body2" gutterBottom>
          Pega el JSON de un producto válido y pulsa "Enviar a Kafka". El producto se enviará directamente a la cola para procesamiento asíncrono.
        </Typography>
        <Alert severity="info" sx={{ mb: 2 }}>
          Valores válidos para <b>"type"</b>: <code>SNEAKERS</code>, <code>CLOTHING</code>, <code>SUPPLEMENT</code>
        </Alert>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
          <Typography variant="body2">Selecciona tipo:</Typography>
          <select
            value={(() => {
              try {
                const parsed = JSON.parse(jsonInput);
                return parsed.type || '';
              } catch { return ''; }
            })()}
            onChange={e => {
              try {
                const parsed = JSON.parse(jsonInput);
                parsed.type = e.target.value;
                setJsonInput(JSON.stringify(parsed, null, 2));
              } catch {}
            }}
            style={{ fontSize: '1rem', padding: '4px 8px' }}
          >
            <option value="">-- Selecciona --</option>
            <option value="SNEAKERS">SNEAKERS</option>
            <option value="CLOTHING">CLOTHING</option>
            <option value="SUPPLEMENT">SUPPLEMENT</option>
          </select>
        </Box>
        <TextField
          label="JSON del producto"
          multiline
          minRows={8}
          maxRows={16}
          fullWidth
          value={jsonInput}
          onChange={e => setJsonInput(e.target.value)}
          variant="outlined"
          margin="normal"
        />
        <Box sx={{ mt: 2, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Button variant="contained" color="secondary" onClick={handleGenerateImage} disabled={imageGenLoading}>
            {imageGenLoading ? 'Generando...' : 'Generar Imagen con IA'}
          </Button>
          <Button variant="contained" color="primary" onClick={handleSendToKafka}>
            Enviar a Kafka
          </Button>
        </Box>

        {imageGenError && <Alert severity="error" sx={{ mt: 2 }}>{imageGenError}</Alert>}
        
        {imageGenLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}>
            <CircularProgress />
          </Box>
        )}

        {generatedImageUrl && (
          <Box sx={{ mt: 2 }}>
            <Typography variant="subtitle1">Imagen Generada:</Typography>
            <img src={generatedImageUrl} alt="Producto generado por IA" style={{ width: '100%', borderRadius: '4px', marginTop: '8px' }} />
          </Box>
        )}

        {result && <Alert severity="success" sx={{ mt: 2 }}>{result}</Alert>}
        {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
      </Paper>
    </Box>
  );
};

export default AdminKafkaPanel;
