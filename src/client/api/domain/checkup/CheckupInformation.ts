type CheckupInformation = {
  id: string;
  description: string;
  dateTime: string;
  status: string;
  animal: AnimalInformation;
  veterinarian: UserPreview;
  clinic: ClinicPreview;
  files: FileInformation[];
};
