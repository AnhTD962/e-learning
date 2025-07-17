import api from './api';

class SearchService {
  searchText(searchRequest) {
    return api.get('/search/text', { params: searchRequest });
  }

  // If you implement handwriting search later, you'd add it here
  // searchHandwriting(handwritingSearchRequest) {
  //   return api.post('/search/handwriting', handwritingSearchRequest);
  // }
}

export default new SearchService();