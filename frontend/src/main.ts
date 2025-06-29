import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes'; // Crée ce fichier juste après
import { provideAnimations } from '@angular/platform-browser/animations';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideAnimations()
    // Tu peux aussi ajouter d'autres providers ici (HTTP, services, interceptors, etc.)
  ]
}).catch(err => console.error(err));

