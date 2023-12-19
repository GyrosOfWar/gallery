<script lang="ts">
  import { enhance } from '$app/forms';

  export let form;

  console.log('form', form);
</script>

<form
  use:enhance
  action="/login"
  method="post"
  class="flex w-full max-w-xl flex-col gap-2 self-center"
>
  <h1 class="mb-4 text-2xl font-bold">Login</h1>
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

  <button type="submit" class="btn btn-success self-end">Submit</button>
</form>

<style lang="postcss">
</style>
