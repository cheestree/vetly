import SecureStore from "expo-secure-store";

const SESSION_TOKEN_KEY = "auth_session_token";
const TOKEN_EXPIRY_KEY = "auth_token_expiry";

async function saveSessionToken(token: string) {
  try {
    const expiryDate = new Date();
    expiryDate.setDate(expiryDate.getDate() + 7);

    await SecureStore.setItemAsync(SESSION_TOKEN_KEY, token);
    await SecureStore.setItemAsync(TOKEN_EXPIRY_KEY, expiryDate.toString());

    return true;
  } catch (e) {
    console.error("Error saving session token:", e);
    return false;
  }
}

async function getSessionToken() {
  try {
    const token = await SecureStore.getItemAsync(SESSION_TOKEN_KEY);
    const expiryString = await SecureStore.getItemAsync(TOKEN_EXPIRY_KEY);

    if (!token || !expiryString) {
      return null;
    }

    const expiryDate = new Date(expiryString);
    const now = new Date();

    if (now > expiryDate) {
      await clearSessionToken();
      return null;
    }

    return token;
  } catch (e) {
    console.error("Error getting session token:", e);
    return null;
  }
}

async function clearSessionToken() {
  try {
    await SecureStore.deleteItemAsync(SESSION_TOKEN_KEY);
    await SecureStore.deleteItemAsync(TOKEN_EXPIRY_KEY);
    return true;
  } catch (e) {
    console.error("Error clearing session token:", e);
    return false;
  }
}

async function isAuthenticated() {
  const token = await getSessionToken();
  return !!token;
}

export default {
  saveSessionToken,
  getSessionToken,
  clearSessionToken,
  isAuthenticated,
};
