const { BASE_URL } = require('../services/config');

function buildContentNodes(content) {
  const value = content || '';
  const nodes = [];
  const imagePattern = /!\[[^\]]*]\(([^)]+)\)/g;
  let lastIndex = 0;
  let match;

  while ((match = imagePattern.exec(value)) !== null) {
    appendTextNode(nodes, value.slice(lastIndex, match.index));
    nodes.push({
      id: `image-${nodes.length}`,
      type: 'image',
      src: resolveContentUrl(match[1])
    });
    lastIndex = match.index + match[0].length;
  }

  appendTextNode(nodes, value.slice(lastIndex));
  return nodes;
}

function appendTextNode(nodes, text) {
  if (!text) {
    return;
  }
  nodes.push({
    id: `text-${nodes.length}`,
    type: 'text',
    text
  });
}

function resolveContentUrl(url) {
  if (!url) {
    return '';
  }
  if (/^https?:\/\//.test(url)) {
    return url;
  }
  if (url.startsWith('/')) {
    return `${BASE_URL}${url}`;
  }
  return url;
}

module.exports = {
  buildContentNodes
};
