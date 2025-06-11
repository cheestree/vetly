import { ApiPaths } from "@/api/Path";
import api from "@/lib/axios";

async function login(token: string): Promise<UserAuthenticated> {
  const response = await api.post(ApiPaths.users.login(), { token: token });
  return response.data;
}

async function logout() {
  const response = await api.post(ApiPaths.users.logout());
  return response.data;
}

async function getUser(id: string): Promise<UserInformation> {
  const response = await api.get(ApiPaths.users.get(id));
  return response.data;
}

async function getUserProfile(): Promise<UserInformation> {
  const response = await api.get(ApiPaths.users.get_user_profile());
  return response.data;
}

async function updateUserProfile(input: UserUpdate): Promise<UserInformation> {
  const response = await api.put(ApiPaths.users.update_user_profile(), input);
  return response.data;
}

export default { login, logout, getUser, getUserProfile, updateUserProfile };
