<script>
  import '../app.pcss';
  import { session } from '$lib/stores';
</script>

<div class="container flex flex-col pt-2">
  {#if $session?.access_token}
    <p>Logged in!</p>
  {:else}
    <p>Not logged in!</p>
  {/if}

  <slot />
</div>
