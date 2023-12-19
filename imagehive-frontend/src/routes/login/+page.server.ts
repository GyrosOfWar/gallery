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
      fail(response.status, { text: response.statusText });
    } else {
      console.log('success');
    }
  },
};
