function isApiRequest(url: URL) {
  return url.pathname.startsWith('/api');
}

export async function handle({ event, resolve }) {
  if (isApiRequest(event.url)) {
    const sessionCookie = event.cookies.get('token');
    if (sessionCookie) {
      event.setHeaders({
        Authorization: `Bearer ${sessionCookie}`,
      });
    }
  }

  const response = await resolve(event);
  return response;
}
