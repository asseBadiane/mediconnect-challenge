import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientProfilePageComponent } from './patient-profile-page.component';

describe('PatientProfilePageComponent', () => {
  let component: PatientProfilePageComponent;
  let fixture: ComponentFixture<PatientProfilePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientProfilePageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
