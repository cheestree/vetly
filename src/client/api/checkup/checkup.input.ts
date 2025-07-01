import { FileInputModel } from "../file/file.input";
import { QueryParams } from "../QueryParams";

type CheckupCreate = {
  animalId: number;
  veterinarianId?: string;
  clinicId: number;
  dateTime: string;
  title: string;
  description: string;
};

type CheckupUpdate = {
  title?: string;
  veterinarianId?: number;
  dateTime?: string;
  description?: string;
  filesToAdd?: FileInputModel[];
  filesToRemove?: number[];
};

type CheckupQueryParams = QueryParams & {
  veterinarianId?: string;
  veterinarianName?: string;
  animalId?: number;
  animalName?: string;
  clinicId?: number;
  clinicName?: string;
  dateTimeStart?: string;
  dateTimeEnd?: string;
};

export { CheckupCreate, CheckupQueryParams, CheckupUpdate };
