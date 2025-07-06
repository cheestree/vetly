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
  dateTime?: string;
  description?: string;
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

