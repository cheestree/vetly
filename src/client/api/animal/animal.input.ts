type AnimalCreate = {
  name: string;
  microchip?: string;
  sex: Sex;
  sterilized: boolean;
  species?: string;
  birthDate?: string;
  imageUrl?: string;
  ownerId?: number;
};

type AnimalUpdate = {
  name?: string;
  microchip?: string;
  sex?: Sex;
  sterilized?: boolean;
  species?: string;
  birthDate?: string;
  imageUrl?: string;
  ownerId?: number;
};

enum Sex {
  MALE,
  FEMALE,
  UNKNOWN,
}

type AnimalQueryParams = QueryParams & {
  userId?: string;
  name?: string;
  microchip?: string;
  birthDate?: number;
  species?: number;
  owned?: boolean;
  self?: boolean | null;
  active?: boolean | null;
};
