import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { SpotifyService } from './spotify-service.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private spotifyService: SpotifyService) {}
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 304) {
          const user = localStorage.getItem('user');
          let userId = '';
          if (user !== null) {
            userId = JSON.parse(user).id;
          }
          return this.spotifyService.refresh(userId).pipe(
            switchMap((newToken: string) => {
              const newRequest = request.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                },
              });
              return next.handle(newRequest);
            })
          );
        } else {
          return throwError(() => error);
        }
      })
    );
  }
}
