import { Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { HeaderComponent } from "../header/header.component";
import { SidebarComponent } from "../sidebar/sidebar.component";
import { FooterComponent } from "../footer/footer.component";
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  imports: [
    CommonModule,
    MatSidenavModule,
    MatListModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    RouterModule,
    HeaderComponent,
    SidebarComponent,
    FooterComponent
  ]
})
export class MainLayoutComponent implements OnInit {

    isHandset$: Observable<boolean>;
    isDarkTheme = false;

    constructor(private breakpointObserver: BreakpointObserver) {
      this.isHandset$ = this.breakpointObserver.observe(Breakpoints.Handset)
        .pipe(
          map(result => result.matches),
          shareReplay()
        );
    }

    ngOnInit(): void {
      // Load theme preference from localStorage
      const savedTheme = localStorage.getItem('mediconnect-theme');
      this.isDarkTheme = savedTheme === 'dark';
      this.applyTheme();
    }

    toggleTheme(): void {
      this.isDarkTheme = !this.isDarkTheme;
      this.applyTheme();
      localStorage.setItem('mediconnect-theme', this.isDarkTheme ? 'dark' : 'light');
    }

    private applyTheme(): void {
      const body = document.body;
      if (this.isDarkTheme) {
        body.classList.add('dark-theme');
      } else {
        body.classList.remove('dark-theme');
      }
    }
}