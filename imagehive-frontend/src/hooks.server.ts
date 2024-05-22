function isApiRequest(url: URL) {
  return url.pathname.startsWith('/api');
}

export async function handleFetch({ request, fetch, event }) {
  if (isApiRequest(event.url)) {
    const cookie = event.cookies.get('token');
    if (cookie) {
      request.headers.append('Authorization', cookie);
    }
  }

  const response = await fetch(request);
  if (response.status === 401) {
    return new Response('Unauthorized', { status: 401 });
  }

  return response;
}
