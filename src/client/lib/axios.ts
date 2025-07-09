import firebase from "@/lib/firebase";
import axios from "axios";

const instance = axios.create({
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
  (error) => {
    console.log(error);
    return Promise.reject(error);
  },
);

export default instance;
