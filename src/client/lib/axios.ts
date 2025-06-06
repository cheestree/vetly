import axios from "axios";
import firebase from "@/lib/firebase";

const instance = axios.create({
  baseURL: "",
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

instance.interceptors.request.use(
  async (config) => {
    const currentUser = firebase.auth.currentUser;
    const token = currentUser ? await currentUser.getIdToken() : null;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

export default instance;
