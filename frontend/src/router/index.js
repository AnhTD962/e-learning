import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Import views
import Home from '../views/Home.vue';
import Login from '../views/Auth/Login.vue';
import Register from '../views/Auth/Register.vue';
import Dashboard from '../views/Dashboard.vue';
import CourseList from '../views/Courses/CourseList.vue';
import CourseDetail from '../views/Courses/CourseDetail.vue';
import LessonDetail from '../views/Lessons/LessonDetail.vue';
import QuizPlay from '../views/Quizzes/QuizPlay.vue';
import FlashcardReview from '../views/Flashcards/FlashcardReview.vue';
import KanjiSearch from '../views/Kanji/KanjiSearch.vue';
import VocabularyList from '../views/Vocabulary/VocabularyList.vue';
import Profile from '../views/Profile.vue';
import UserManagement from '../views/Admin/UserManagement.vue';
import CourseManagement from '../views/Admin/CourseManagement.vue';
import QuizManagement from '../views/Admin/QuizManagement.vue';
import FlashcardSetManagement from '../views/Admin/FlashcardSetManagement.vue';
import KanjiManagement from '../views/Admin/KanjiManagement.vue';
import VocabularyManagement from '../views/Admin/VocabularyManagement.vue';
import ProgressOverview from '../views/Admin/ProgressOverview.vue';
import AchievementManagement from '../views/Admin/AchievementManagement.vue';
import ContentModeration from '../views/Admin/ContentModeration.vue';
import StudySessionOverview from '../views/Admin/StudySessionOverview.vue';


const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/courses',
    name: 'CourseList',
    component: CourseList,
    meta: { requiresAuth: true }
  },
  {
    path: '/courses/:id',
    name: 'CourseDetail',
    component: CourseDetail,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/courses/:courseId/modules/:moduleId/lessons/:lessonId',
    name: 'LessonDetail',
    component: LessonDetail,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/lessons/:lessonId/quizzes/:quizId/play',
    name: 'QuizPlay',
    component: QuizPlay,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/lessons/:lessonId/flashcards/:flashcardSetId/review',
    name: 'FlashcardReview',
    component: FlashcardReview,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/kanji',
    name: 'KanjiSearch',
    component: KanjiSearch,
    meta: { requiresAuth: true }
  },
  {
    path: '/kanji/:id',
    name: 'KanjiDetail',
    component: KanjiSearch, // Reusing KanjiSearch for detail for now, could be separate
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/vocabulary',
    name: 'VocabularyList',
    component: VocabularyList,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  // Admin Routes
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/courses',
    name: 'AdminCourseManagement',
    component: CourseManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/quizzes',
    name: 'AdminQuizManagement',
    component: QuizManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/flashcards',
    name: 'AdminFlashcardSetManagement',
    component: FlashcardSetManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/kanji',
    name: 'AdminKanjiManagement',
    component: KanjiManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/vocabulary',
    name: 'AdminVocabularyManagement',
    component: VocabularyManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/progress',
    name: 'AdminProgressOverview',
    component: ProgressOverview,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/achievements',
    name: 'AdminAchievementManagement',
    component: AchievementManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/moderation',
    name: 'AdminContentModeration',
    component: ContentModeration,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/study-sessions',
    name: 'AdminStudySessionOverview',
    component: StudySessionOverview,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../views/NotFound.vue') }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;
  const userRoles = authStore.userRoles; // Assuming roles are stored in auth store

  if (to.meta.requiresAuth && !isAuthenticated) {
    // If route requires authentication and user is not logged in, redirect to login
    next({ name: 'Login' });
  } else if (to.meta.requiresAdmin && (!isAuthenticated || !userRoles.includes('ADMIN'))) {
    // If route requires admin and user is not admin, redirect to dashboard or home
    next({ name: 'Dashboard' }); // Or a 403 Forbidden page
  } else if ((to.name === 'Login' || to.name === 'Register') && isAuthenticated) {
    // If user is already logged in, redirect from login/register pages
    next({ name: 'Dashboard' });
  } else {
    next(); // Proceed to the route
  }
});

export default router;