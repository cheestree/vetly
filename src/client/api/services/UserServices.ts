import { ApiPaths } from "../http/Path";
import api from '@/lib/axios'

async function login(token: string): Promise<UserAuthenticated> {
  return api.post(ApiPaths.users.login(), { token: token })
}

async function logout() {
  return api.post(ApiPaths.users.logout())
}

async function getUser(userId: string): Promise<UserInformation> {
  const response = await api.get(ApiPaths.users.get(userId))
  return response.data
}

async function getUserProfile(): Promise<UserInformation> {
  const response = await api.get(ApiPaths.users.get_user_profile())
  return response.data
}

export default { login, logout, getUser, getUserProfile };
