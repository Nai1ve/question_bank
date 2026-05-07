const { DEBUG_FLAGS } = require('../services/config');

let requestSequence = 0;

function isDebugEnabled(flagName) {
  return !!(DEBUG_FLAGS && DEBUG_FLAGS[flagName]);
}

function nextRequestSequence() {
  requestSequence += 1;
  return requestSequence;
}

function logInfo(scope, message, payload) {
  if (!isDebugEnabled('network')) {
    return;
  }

  if (payload === undefined) {
    console.info(`[onepass][${scope}] ${message}`);
    return;
  }

  console.info(`[onepass][${scope}] ${message}`, payload);
}

function logWarn(scope, message, payload) {
  if (!isDebugEnabled('network')) {
    return;
  }

  if (payload === undefined) {
    console.warn(`[onepass][${scope}] ${message}`);
    return;
  }

  console.warn(`[onepass][${scope}] ${message}`, payload);
}

function logError(scope, message, payload) {
  if (!isDebugEnabled('network')) {
    return;
  }

  if (payload === undefined) {
    console.error(`[onepass][${scope}] ${message}`);
    return;
  }

  console.error(`[onepass][${scope}] ${message}`, payload);
}

module.exports = {
  isDebugEnabled,
  nextRequestSequence,
  logInfo,
  logWarn,
  logError
};
