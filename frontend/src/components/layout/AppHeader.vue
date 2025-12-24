<template>
  <header class="app-header">
    <div class="logo">
      <img src="@/assets/logo.png" alt="Food Finder" class="logo-icon" />
      <span class="logo-text">Food Finder</span>
    </div>
  </header>
</template>

<script setup>
</script>

<style scoped>
.app-header {
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #FF6B6B;
}
</style>
