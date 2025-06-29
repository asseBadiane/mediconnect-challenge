import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDivider } from '@angular/material/divider';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule,
    MatBadgeModule,
    MatDivider
  ]
})
export class HeaderComponent {
  @Input() drawer!: MatSidenav;
  @Input() isDarkTheme = false;
  @Output() themeToggle = new EventEmitter<void>();

  constructor(private router: Router) {}

  toggleTheme(): void {
    this.themeToggle.emit();
  }

  navigateToSearch(): void {
    this.router.navigate(['/patients']);
  }
}