const BASE_URL = 'http://127.0.0.1:8080';
const MOCK_FLAGS = {
  auth: false,
  category: false,
  practice: false,
  student: false,
  tag: false,
  vocabulary: false,
  recite: false,
  wrongBook: false
};
const DEBUG_FLAGS = {
  network: true
};

module.exports = {
  BASE_URL,
  MOCK_FLAGS,
  DEBUG_FLAGS
};
