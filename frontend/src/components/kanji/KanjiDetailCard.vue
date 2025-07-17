<template>
  <div class="bg-white p-6 rounded-xl shadow-md border border-gray-200">
    <h3 class="text-5xl font-bold text-center text-gray-800 mb-4">{{ kanji.kanjiCharacter }}</h3>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-gray-700 mb-6">
      <div>
        <p class="text-lg font-semibold mb-1">Meaning:</p>
        <p class="text-xl">{{ kanji.meaning }}</p>
      </div>
      <div>
        <p class="text-lg font-semibold mb-1">Furigana:</p>
        <p class="text-xl">{{ kanji.furigana }}</p>
      </div>
      <div>
        <p class="text-lg font-semibold mb-1">On'yomi:</p>
        <p class="text-xl">{{ kanji.onyomi }}</p>
      </div>
      <div>
        <p class="text-lg font-semibold mb-1">Kun'yomi:</p>
        <p class="text-xl">{{ kanji.kunyomi }}</p>
      </div>
    </div>

    <div class="mb-6">
      <p class="text-lg font-semibold mb-1">JLPT Level:</p>
      <span class="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-semibold">
        {{ kanji.jlptLevel || 'N/A' }}
      </span>
    </div>

    <div class="mb-6">
      <p class="text-lg font-semibold mb-1">Stroke Count:</p>
      <p class="text-xl">{{ kanji.strokeCount }}</p>
    </div>

    <div v-if="kanji.strokeOrderSvg" class="mb-6">
      <p class="text-lg font-semibold mb-2">Stroke Order:</p>
      <!-- WARNING: Using v-html can expose to XSS attacks if SVG content is not sanitized.
           For production, ensure your backend sanitizes this or use a safe SVG rendering library. -->
      <div v-html="kanji.strokeOrderSvg" class="border border-gray-300 rounded-lg p-2 bg-white w-32 h-32 flex items-center justify-center mx-auto"></div>
    </div>

    <div v-if="kanji.examples && kanji.examples.length > 0" class="mb-6">
      <p class="text-lg font-semibold mb-2">Examples:</p>
      <ul class="list-disc list-inside space-y-1">
        <li v-for="(example, idx) in kanji.examples" :key="idx" class="text-gray-700">{{ example }}</li>
      </ul>
    </div>

    <div v-if="kanji.radicals && kanji.radicals.length > 0">
      <p class="text-lg font-semibold mb-2">Radicals:</p>
      <div class="flex flex-wrap gap-2">
        <span v-for="(radical, idx) in kanji.radicals" :key="idx" class="bg-gray-200 text-gray-800 px-3 py-1 rounded-full text-sm">
          {{ radical }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>

const props = defineProps({
  kanji: {
    type: Object,
    required: true,
    // Expected structure: { id, kanjiCharacter, onyomi, kunyomi, meaning, furigana, strokeCount, strokeOrderSvg, examples, radicals, jlptLevel }
  },
});
</script>

<style scoped>
/* No specific styles needed, Tailwind handles most. */
/* You might want to add specific styles for SVG rendering if needed */
</style>