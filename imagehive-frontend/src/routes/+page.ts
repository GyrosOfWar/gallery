import { Configuration, DefaultApi } from 'imagehive-client';

export async function load(event) {
  const config = new Configuration({
    fetchApi: event.fetch,
  });
  const client = new DefaultApi(config);

  const images = await client.getImages({
    pageable: {
      size: 20,
      number: 0,
      orderBy: [],
      sort: {
        orderBy: [],
      },
    },
  });

  return images;
}
