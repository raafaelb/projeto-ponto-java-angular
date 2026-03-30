import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: router }]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should store token and user on login', () => {
    service.login('admin', 'admin123').subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({
      token: 'token123',
      expiresIn: 1000,
      message: 'ok',
      user: {
        id: 1,
        username: 'admin',
        name: 'Administrador',
        email: 'admin@sistema.com',
        role: 'ADMIN'
      }
    });

    expect(localStorage.getItem('auth_token')).toBe('token123');
    expect(service.getUserRole()).toBe('ADMIN');
  });

  it('should redirect by role', () => {
    localStorage.setItem('auth_token', 'x');
    localStorage.setItem(
      'user_info',
      JSON.stringify({ id: 1, username: 'empresa', name: 'Empresa', email: 'e@s.com', role: 'COMPANY' })
    );

    service.redirectByRole();

    expect(router.navigate).toHaveBeenCalledWith(['/company/dashboard']);
  });
});
