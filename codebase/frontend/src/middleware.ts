import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('medcore_token')?.value;
  const rolesString = request.cookies.get('medcore_roles')?.value || '';
  const roles = rolesString.split(',').map(r => r.trim().toUpperCase());
  const isAdmin = roles.includes('ROLE_ADMIN');
  
  const path = request.nextUrl.pathname;
  const isAuthPage = path.startsWith('/login') || path.startsWith('/register');

  // 1. Authentication Check
  if (!token && !isAuthPage) {
    return NextResponse.redirect(new URL('/login', request.url));
  }
  if (token && isAuthPage) {
    return NextResponse.redirect(new URL('/', request.url));
  }

  // 2. Authorization / RBAC Check
  if (token && !isAdmin) {
    // Define role requirements for base paths
    const roleRequirements: Record<string, string[]> = {
      '/doctor': ['ROLE_DOCTOR'],
      '/nursing': ['ROLE_NURSE'],
      '/pharmacy': ['ROLE_PHARMACIST'],
      '/lab': ['ROLE_LAB_TECH'],
      '/radiology': ['ROLE_RADIOLOGIST'],
      '/billing': ['ROLE_BILLER', 'ROLE_RECEPTIONIST'],
      '/inventory': ['ROLE_INVENTORY_MANAGER', 'ROLE_PHARMACIST'],
      '/ot': ['ROLE_DOCTOR', 'ROLE_NURSE'],
      '/icu': ['ROLE_DOCTOR', 'ROLE_NURSE'],
      '/admissions': ['ROLE_RECEPTIONIST', 'ROLE_NURSE'],
      '/opd': ['ROLE_RECEPTIONIST', 'ROLE_DOCTOR', 'ROLE_NURSE'],
      '/patients': ['ROLE_RECEPTIONIST', 'ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_PHARMACIST', 'ROLE_LAB_TECH', 'ROLE_RADIOLOGIST', 'ROLE_BILLER'],
      '/admin': [] // Handled by isAdmin above
    };

    for (const [route, allowedRoles] of Object.entries(roleRequirements)) {
      if (path.startsWith(route)) {
        // If route matches, user must have at least one of the allowed roles
        const hasAccess = allowedRoles.some(r => roles.includes(r));
        if (!hasAccess) {
          // Redirect unauthorized users to dashboard
          return NextResponse.redirect(new URL('/', request.url));
        }
      }
    }
  }

  return NextResponse.next();
}

// Only run middleware on the root and specific sub-paths, avoiding static assets and public files
export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico|logo.png|.*\\.(?:png|jpg|jpeg|gif|svg|webp|ico|css|js)).*)',
  ],
};
