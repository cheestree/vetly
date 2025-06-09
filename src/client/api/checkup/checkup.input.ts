type CheckupCreate = {
    animalId: number;
    veterinarianId: number;
    clinicId: number;
    dateTime: string;
    title: string;
    description: string;
    files: FileInputModel[]
}

type CheckupUpdate = {
    title?: string;
    veterinarianId?: number;
    dateTime?: string;
    description?: string;
    filesToAdd?: FileInputModel[];
    filesToRemove?: number[];
}

type CheckupQueryParams = QueryParams & {
  veterinarianId?: number;
  veterinarianName?: string;
  animalId?: number;
  animalName?: string;
  clinicId?: number;
  clinicName?: string;
  dateTimeStart?: string;
  dateTimeEnd?: string;
};