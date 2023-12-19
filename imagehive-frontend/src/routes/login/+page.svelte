<script lang="ts">
  import { enhance } from '$app/forms';
  import { session } from '$lib/stores.js';
  import { XOctagon, Check } from 'lucide-svelte';

  export let form;

  function onSubmit() {
    if (form && !form.error) {
      $session = form.token;
    }
  }
</script>

<form
  action="/login"
  method="post"
  class="flex w-full max-w-xl flex-col gap-2 self-center"
  use:enhance
  on:submit={onSubmit}
>
  <h1 class="mb-4 text-2xl font-bold">Login</h1>
  {#if form != null && form.error}
    <div class="alert alert-error">
      <XOctagon class="h-6 w-6" />
      <p>Error: {form.error}</p>
    </div>
  {/if}

  <div class="form-control">
    <label class="label" for="username">
      <span class="label-text">Username</span>
    </label>
    <input class="input input-bordered" name="username" type="text" placeholder="Username" />
  </div>
  <div class="form-control">
    <label class="label" for="password">
      <span class="label-text">Password</span>
    </label>
    <input class="input input-bordered" name="password" type="password" placeholder="Password" />
  </div>

  <button type="submit" class="btn btn-success self-end">
    <Check />
    Submit</button
  >
</form>

<style lang="postcss">
</style>
