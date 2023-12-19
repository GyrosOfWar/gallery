import { writable } from 'svelte/store';
import type { JwtSession } from './types';

export const session = writable<JwtSession | undefined>();
