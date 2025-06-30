// src/app/features/conditions/components/condition-list/condition-list.component.ts
import { Component, OnInit, OnDestroy, TrackByFunction } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ConditionDto } from '../../../shared/models/condition.model';
import { ConditionService } from '../../../core/services/condition.service';

import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router'; // Import RouterModule for navigation


@Component({
  selector: 'app-condition-list',
  templateUrl: './condition-list.component.html',
  styleUrls: ['./condition-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ] // Add necessary Angular modules or components here
})
export class ConditionListComponent implements OnInit, OnDestroy {
  conditions: ConditionDto[] = [];
  filteredConditions: ConditionDto[] = [];
  searchTerm: string = '';
  isLoading: boolean = false;
  selectedConditions: Set<number> = new Set();
  
  private destroy$ = new Subject<void>();
 trackByConditionId!: TrackByFunction<ConditionDto>;

  constructor(
    private conditionService: ConditionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadConditions();
    this.subscribeToConditions();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadConditions(): void {
    this.isLoading = true;
    this.conditionService.getAllConditions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (conditions) => {
          this.conditions = conditions;
          this.filteredConditions = conditions;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading conditions:', error);
          this.isLoading = false;
        }
      });
  }

  private subscribeToConditions(): void {
    this.conditionService.conditions$
      .pipe(takeUntil(this.destroy$))
      .subscribe(conditions => {
        this.conditions = conditions;
        this.applyFilter();
      });
  }

  onSearch(): void {
    this.applyFilter();
  }

  private applyFilter(): void {
    if (!this.searchTerm.trim()) {
      this.filteredConditions = [...this.conditions];
      return;
    }

    const searchLower = this.searchTerm.toLowerCase();
    this.filteredConditions = this.conditions.filter(condition =>
      condition.codeDisplay?.toLowerCase().includes(searchLower) ||
      condition.patientReference?.toLowerCase().includes(searchLower) ||
      condition.clinicalStatus?.toLowerCase().includes(searchLower) ||
      condition.note?.toLowerCase().includes(searchLower)
    );
  }

  onAddCondition(): void {
    this.router.navigate(['/conditions/new']);
  }

  onViewCondition(condition: ConditionDto): void {
    this.router.navigate(['/conditions', condition.fhirId]);
  }

  onEditCondition(condition: ConditionDto): void {
    this.router.navigate(['/conditions', condition.fhirId, 'edit']);
  }

  onDeleteCondition(condition: ConditionDto): void {
    if (confirm(`Are you sure you want to delete the condition "${condition.codeDisplay}"?`)) {
      // Note: You'll need to implement delete functionality in the service and backend
      console.log('Delete condition:', condition.fhirId);
    }
  }

  onConditionSelect(conditionId: number): void {
    if (this.selectedConditions.has(conditionId)) {
      this.selectedConditions.delete(conditionId);
    } else {
      this.selectedConditions.add(conditionId);
    }
  }

  onSelectAll(): void {
    if (this.selectedConditions.size === this.filteredConditions.length) {
      this.selectedConditions.clear();
    } else {
      this.selectedConditions.clear();
      this.filteredConditions.forEach(condition => {
        if (condition.id) {
          this.selectedConditions.add(condition.id);
        }
      });
    }
  }

  isSelected(conditionId: number | undefined): boolean {
    return conditionId ? this.selectedConditions.has(conditionId) : false;
  }

  isAllSelected(): boolean {
    return this.selectedConditions.size === this.filteredConditions.length && this.filteredConditions.length > 0;
  }

  formatDate(dateString?: string): string {
    if (!dateString) return 'N/A';
    
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return 'Invalid Date';
    }
  }

  getStatusBadgeClass(status?: string): string {
    switch (status?.toLowerCase()) {
      case 'active':
        return 'badge-success';
      case 'inactive':
        return 'badge-secondary';
      case 'resolved':
        return 'badge-info';
      case 'remission':
        return 'badge-warning';
      default:
        return 'badge-light';
    }
  }

  getSeverityBadgeClass(severity?: string): string {
    switch (severity?.toLowerCase()) {
      case 'severe':
        return 'badge-danger';
      case 'moderate':
        return 'badge-warning';
      case 'mild':
        return 'badge-info';
      default:
        return 'badge-light';
    }
  }

  getPatientNameFromReference(reference?: string): string {
    if (!reference) return 'Unknown Patient';
    
    // Extract patient ID from reference (e.g., "Patient/123" -> "123")
    const patientId = reference.replace('Patient/', '');
    return `Patient ${patientId}`;
  }

  onRefresh(): void {
    this.loadConditions();
  }

  getTotalConditions(): number {
    return this.conditions.length;
  }

  getActiveConditionsCount(): number {
    return this.conditions.filter(c => c.clinicalStatus?.toLowerCase() === 'active').length;
  }
}