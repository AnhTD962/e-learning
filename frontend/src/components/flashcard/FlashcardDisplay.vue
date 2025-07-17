<template>
  <div
    class="flashcard bg-white rounded-xl shadow-lg p-8 cursor-pointer transform transition-transform duration-500 ease-in-out perspective-1000"
    :class="{ 'is-flipped': isFlipped }"
    @click="flipCard"
  >
    <div class="flashcard-inner relative w-full h-full text-center">
      <!-- Front of the card -->
      <div class="flashcard-face flashcard-front absolute w-full h-full flex flex-col items-center justify-center backface-hidden">
        <h3 class="text-4xl font-bold text-gray-800 mb-4">{{ card.front }}</h3>
        <p v-if="card.furigana" class="text-xl text-gray-600 mb-2">({{ card.furigana }})</p>
        <p v-if="card.romaji" class="text-lg text-gray-500">{{ card.romaji }}</p>
      </div>

      <!-- Back of the card -->
      <div class="flashcard-face flashcard-back absolute w-full h-full flex flex-col items-center justify-center backface-hidden rotate-y-180">
        <p class="text-3xl font-semibold text-gray-800 mb-4">{{ card.back }}</p>
        <p v-if="card.exampleSentence" class="text-md text-gray-600 italic px-4">"{{ card.exampleSentence }}"</p>
        <!-- Optional: Add audio playback if audioUrl is available -->
        <button v-if="card.audioUrl" @click.stop="playAudio" class="mt-4 bg-blue-500 hover:bg-blue-600 text-white p-2 rounded-full shadow-md transition duration-300">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M9.383 3.076A1 1 0 0110 4v12a1 1 0 01-1.707.707L4.586 13H2a1 1 0 01-1-1V8a1 1 0 011-1h2.586l3.707-3.707a1 1 0 011.09-.117zM14.009 7.77a.75.75 0 01.025 1.06L12.5 10l1.534 1.17a.75.75 0 01-.025 1.06l-.05.05a.75.75 0 01-1.06-.025L11 10l-1.475 1.135a.75.75 0 01-1.06-.025l-.05-.05a.75.75 0 01-.025-1.06L9.5 10l-1.534-1.17a.75.75 0 01.025-1.06l.05-.05A.75.75 0 019 7.5l1.475 1.135L12 7.5l1.534-1.17a.75.75 0 011.06.025l.05.05z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  card: {
    type: Object,
    required: true,
    // Expected card structure: { id, front, back, furigana, romaji, exampleSentence, audioUrl, imageUrl }
  },
});

const isFlipped = ref(false);

const flipCard = () => {
  isFlipped.value = !isFlipped.value;
};

const playAudio = () => {
  if (props.card.audioUrl) {
    const audio = new Audio(props.card.audioUrl);
    audio.play().catch(e => console.error("Error playing audio:", e));
  }
};
</script>

<style scoped>
.flashcard {
  width: 100%;
  height: 300px; /* Fixed height for consistency */
  border: 1px solid #e2e8f0; /* Tailwind gray-200 */
}

.flashcard-inner {
  transform-style: preserve-3d;
  transition: transform 0.5s;
}

.flashcard.is-flipped .flashcard-inner {
  transform: rotateY(180deg);
}

.flashcard-face {
  -webkit-backface-visibility: hidden; /* For Safari */
  backface-visibility: hidden;
}

.flashcard-back {
  transform: rotateY(180deg);
}

/* Ensure content is centered vertically and horizontally */
.flashcard-face {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>