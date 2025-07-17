import api from './api';

class AchievementService {
  createAchievement(achievementData) {
    return api.post('/achievements', achievementData);
  }

  getAchievementById(id) {
    return api.get(`/achievements/${id}`);
  }

  getAllAchievements() {
    return api.get('/achievements');
  }

  updateAchievement(id, achievementData) {
    return api.put(`/achievements/${id}`, achievementData);
  }

  deleteAchievement(id) {
    return api.delete(`/achievements/${id}`);
  }

  grantAchievementToUser(userId, achievementId) {
    return api.post(`/achievements/${achievementId}/grant-to-user/${userId}`);
  }

  checkMyAchievements() {
    return api.post('/achievements/check-my-achievements');
  }

  getUserGrantedAchievements(userId) {
    return api.get(`/achievements/user/${userId}`);
  }
}

export default new AchievementService();