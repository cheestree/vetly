import { ApiPaths } from "../http/Path";
import auth from "../../lib/firebase";
import { buildFetchOptions } from "../Utils";

async function getUser(userId: string): Promise<UserInformation> {
  const idToken = await auth.auth.currentUser?.getIdToken(false);

  return fetch(
    ApiPaths.users.get(userId),
    buildFetchOptions("GET", undefined, idToken),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching user:", error);
      throw error;
    });
}

async function getUserProfile(): Promise<UserInformation> {
  const idToken = await auth.auth.currentUser?.getIdToken(false);

  return fetch(
    ApiPaths.users.get_user_profile(),
    buildFetchOptions("GET", undefined, idToken),
  )
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching user profile:", error);
      throw error;
    });
}

export default { getUser, getUserProfile };
