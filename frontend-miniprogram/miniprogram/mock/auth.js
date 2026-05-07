const mockUser = {
  id: 1001,
  displayName: '微信用户',
  avatarUrl: ''
};

function createMockLoginResult(code) {
  return {
    token: `mock-jwt-${code || 'fallback-code'}`,
    user: mockUser
  };
}

module.exports = {
  mockUser,
  createMockLoginResult
};

