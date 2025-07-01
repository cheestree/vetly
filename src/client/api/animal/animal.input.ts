import { QueryParams } from "../QueryParams";

type AnimalCreate = {
  name: string;
  microchip?: string;
  sex: Sex;
  sterilized: boolean;
  species?: string;
  birthDate?: string;
  ownerId?: string;
};

type AnimalUpdate = {
  name?: string;
  microchip?: string;
  sex?: Sex;
  sterilized?: boolean;
  species?: string;
  birthDate?: string;
  ownerId?: string;
};

type AnimalQueryParams = QueryParams & {
  userId?: string;
  name?: string;
  microchip?: string;
  birthDate?: string;
  species?: string;
  owned?: boolean;
  self?: boolean | null;
  active?: boolean | null;
};

enum Sex {
  MALE = "MALE",
  FEMALE = "FEMALE",
  UNKNOWN = "UNKNOWN",
}

export { AnimalCreate, AnimalQueryParams, AnimalUpdate, Sex };
