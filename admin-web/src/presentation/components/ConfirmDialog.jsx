import React from 'react';
import './ConfirmDialog.css';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';

const ConfirmDialog = ({ isOpen, title, message, onConfirm, onCancel, confirmText = 'Eliminar', cancelText = 'Cancelar' }) => {
  if (!isOpen) return null;

  return (
    <div className="confirm-dialog-overlay">
      <div className="confirm-dialog-card">
        <div className="confirm-dialog-icon">
          <WarningAmberIcon />
        </div>
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="confirm-dialog-actions">
          <button onClick={onCancel} className="confirm-cancel-btn">
            {cancelText}
          </button>
          <button onClick={onConfirm} className="confirm-delete-btn">
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmDialog;
