import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard/dashboard.component';
import { PatientListComponent } from './features/patients/components/patient-list/patient-list.component';
import { PatientFormComponent } from './features/patients/components/patient-form/patient-form.component';
import { PatientProfilePageComponent } from './features/patients/components/patient-profile-page/patient-profile-page.component';
import { UpdatePatientComponent } from './features/patients/components/update-patient/update-patient.component';
import { ConditionListComponent } from './features/conditions/condition-list/condition-list.component';


export const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
  },
  {
    path: 'patients',
    component: PatientListComponent
  },
  {
    path: 'patients/new',
    component: PatientFormComponent
  },
  {
    path: 'patients/:fhirId',
    component: PatientProfilePageComponent
  },
  {
    path: 'patients/:fhirId/edit',
    component: UpdatePatientComponent
  },
  {
    path: 'conditions',
    component: ConditionListComponent
  }
];