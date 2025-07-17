<template>
  <div class="container mx-auto py-8">
    <div v-if="kanjiStore.loading" class="flex justify-center">
      <LoadingSpinner />
    </div>

    <div v-else-if="kanjiStore.error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative" role="alert">
      <span class="block sm:inline">{{ kanjiStore.error }}</span>
    </div>

    <div v-else-if="!kanjiStore.currentKanji" class="text-center text-gray-500 mt-8">
      <p>Kanji entry not found.</p>
      <router-link to="/kanji" class="text-blue-600 hover:underline mt-4 block">Back to Kanji Search</router-link>
    </div>

    <div v-else class="max-w-2xl mx-auto">
      <KanjiDetailCard :kanji="kanjiStore.currentKanji" />
      <div class="mt-8 text-center">
        <router-link to="/kanji" class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-5 rounded-lg shadow-md transition duration-300">
          Back to Kanji Search
        </router-link>
      </div>
    </div>

    <MessageBox
      v-model="showMessageBox"
      :type="messageBoxType"
      :title="messageBoxTitle"
      :message="messageBoxMessage"
      @confirmed="showMessageBox = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useKanjiStore } from '../../stores/kanji';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import MessageBox from '../../components/common/MessageBox.vue';
import KanjiDetailCard from '../../components/kanji/KanjiDetailCard.vue';

const route = useRoute();
const kanjiStore = useKanjiStore();

const kanjiId = computed(() => route.params.id);

const showMessageBox = ref(false);
const messageBoxType = ref('info');
const messageBoxTitle = ref('');
const messageBoxMessage = ref('');

const fetchKanjiData = async () => {
  await kanjiStore.fetchKanjiById(kanjiId.value);
};

onMounted(() => {
  fetchKanjiData();
});

watch(kanjiId, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchKanjiData();
  }
});

const showMessage = (type, title, message) => {
  messageBoxType.value = type;
  messageBoxTitle.value = title;
  messageBoxMessage.value = message;
  showMessageBox.value = true;
};
</script>

<style scoped>
/* No specific styles needed, Tailwind handles it */
</style>