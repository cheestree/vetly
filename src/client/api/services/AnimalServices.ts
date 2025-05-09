import { ApiPaths } from "../http/Path";

async function getAnimal(animalId: string): Promise<AnimalInformation> {
  return fetch(ApiPaths.root + ApiPaths.animals.get(animalId))
    .then((response) => response.json())
    .catch((error) => {
      console.error("Error fetching animal:", error);
      throw error;
    });
}

export default { getAnimal };
