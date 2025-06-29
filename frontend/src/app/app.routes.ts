import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/components/dashboard/dashboard.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  
  
  {
    path: 'dashboard',
    component: DashboardComponent,
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