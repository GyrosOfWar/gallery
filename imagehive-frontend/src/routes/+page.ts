export async function load(event) {
  // const config = new Configuration({
  //   fetchApi: event.fetch,
  // });
  // const client = new DefaultApi(config);

  const response = await event.fetch('/api/images');
  const images = await response.json();

  return images;
}
