import type { JwtSession } from './lib/types';

// See https://kit.svelte.dev/docs/types#app
// for information about these interfaces
declare global {
  namespace App {
    // interface Error {}
    interface Locals {
      session?: JwtSession;
    }
    // interface PageData {}
    // interface PageState {}
    // interface Platform {}
  }
}

export {};
