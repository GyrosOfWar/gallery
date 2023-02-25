export default class QueryStringHelper {
  inner: URLSearchParams

  constructor(url: string) {
    this.inner = new URL(url).searchParams
  }

  getNumber(name: string, defaultValue: number): number {
    const string = this.inner.get(name)
    if (string) {
      const parsed = parseInt(string, 10)
      if (!isNaN(parsed)) {
        return parsed
      }
    }
    return defaultValue
  }

  getString(name: string, defaultValue: string): string {
    const string = this.inner.get(name)
    return string || defaultValue
  }

  getStringList(name: string): string[] {
    return this.inner.getAll(name)
  }
}
