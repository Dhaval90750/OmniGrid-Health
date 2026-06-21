import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  // Check for the omnigrid_token cookie
  const token = request.cookies.get('omnigrid_token')?.value;
  const isLoginPage = request.nextUrl.pathname.startsWith('/login');

  // If trying to access a protected route without a token, redirect to login
  if (!token && !isLoginPage) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  // If trying to access the login page while already authenticated, redirect to dashboard
  if (token && isLoginPage) {
    return NextResponse.redirect(new URL('/', request.url));
  }

  return NextResponse.next();
}

// Only run middleware on the root and specific sub-paths, avoiding static assets
export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};
