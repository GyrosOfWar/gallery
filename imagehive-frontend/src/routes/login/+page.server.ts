import type { JwtSession } from '$lib/types.js';
import { fail } from '@sveltejs/kit';

export const actions = {
  async default(event) {
    const form = await event.request.formData();
    const username = form.get('username');
    const password = form.get('password');

    const response = await event.fetch('/api/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
      headers: {
        'content-type': 'application/json',
      },
    });

    if (!response.ok) {
      return fail(response.status, { success: false, error: 'Bad credentials' });
    } else {
      const token = (await response.json()) as JwtSession;

      event.cookies.set('token', `Bearer ${token.access_token}`, {
        httpOnly: true,
        path: '/',
        secure: true,
        sameSite: 'strict',
        maxAge: token.expires_in,
      });

      return { success: true, token };
    }
  },
};
