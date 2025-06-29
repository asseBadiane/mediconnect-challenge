import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  {
    path: 'patients',
    loadChildren: () => import('./features/patients/patients.module').then(m => m.PatientsModule)
  },
  {
    path: 'conditions',
    loadChildren: () => import('./features/conditions/conditions.module').then(m => m.ConditionsModule)
  }
];
