<template>
    <div class="container" style="max-width: 1200px;">
        <router-view />
    </div>
</template>

<script setup>
// 这是一个容器组件，用于全局限制内容宽度，保证内容不铺满整个屏幕，更符合设计感。
</script>

<style scoped>
/* 样式将在全局 main.css 中定义，这里仅作内容包裹 */
.container {
    padding: 0 20px;
    max-width: 1200px;
    margin: 0 auto;
}
</style>