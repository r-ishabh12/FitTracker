export type UserRole = 'USER' | 'ADMIN';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export type ActivityType = 'RUNNING' | 'WALKING' | 'CYCLING' | 'SWIMMING' | 'WEIGHT_TRAINING' | 'YOGA' | 'HIIT' | 'CARDIO' | 'STRETCHING' | 'OTHER';

export interface Activity {
  id: string;
  userId: string;
  type: ActivityType;
  additionalMetrics: Record<string, unknown> | null;
  duration: number;
  caloriesBurned: number | null;
  startTime: string;
  createdAt: string;
  updatedAt: string;
}

export interface ActivityInput {
  type: ActivityType;
  duration: number;
  caloriesBurned: number | null;
  startTime: string;
  additionalMetrics: Record<string, unknown>;
}

export interface DailyActivity {
  day: string;
  activities: number;
  minutes: number;
  calories: number;
}

export interface DashboardSummary {
  totalActivities: number;
  totalDurationMinutes: number;
  totalCalories: number;
  weeklyActivity: DailyActivity[];
  recentActivities: Activity[];
}

export interface Recommendation {
  id: string;
  activityId: string;
  activityType: ActivityType;
  type: string;
  recommendation: string;
  improvements: string[];
  suggestions: string[];
  safety: string[];
  createdAt: string;
}
