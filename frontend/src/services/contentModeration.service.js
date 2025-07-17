import api from './api';

class ContentModerationService {
  createModerationLog(logData) {
    return api.post('/moderation-logs', logData);
  }

  getModerationLogById(id) {
    return api.get(`/moderation-logs/${id}`);
  }

  getAllModerationLogs() {
    return api.get('/moderation-logs');
  }

  updateModerationLog(id, logData) {
    return api.put(`/moderation-logs/${id}`, logData);
  }

  deleteModerationLog(id) {
    return api.delete(`/moderation-logs/${id}`);
  }
}

export default new ContentModerationService();