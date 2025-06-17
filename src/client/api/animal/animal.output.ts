import { Sex } from "./animal.input";

type AnimalPreview = {
  id: number;
  name: string;
  species?: string;
  birthDate?: string;
  imageUrl?: string;
  age?: number;
  owner?: UserPreview;
};

type AnimalInformation = {
  id: string;
  name: string;
  microchip?: string;
  sex: Sex;
  sterilized: boolean;
  species?: string;
  birthDate?: string;
  imageUrl?: string;
  age?: string;
  owner?: UserPreview;
};

export { AnimalInformation, AnimalPreview };
