import { FileInformation, FilePreview } from "../file/file.output";
import { UserPreview } from "../user/user.output";
import { Sex } from "./animal.input";

type AnimalPreview = {
  id: number;
  name: string;
  species?: string;
  birthDate?: string;
  image?: FilePreview;
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
  image?: FileInformation;
  age?: string;
  owner?: UserPreview;
};

export { AnimalInformation, AnimalPreview };

