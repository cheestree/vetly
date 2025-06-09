type CheckupInformation = {
  id: string;
  title: string;
  description: string;
  dateTime: string;
  status: string;
  animal: AnimalInformation;
  veterinarian: UserPreview;
  clinic: ClinicPreview;
  files: FileInformation[];
};

type CheckupPreview = {
  id: number;
  title: string;
  description: string;
  dateTime: string;
  status: string;
  animal: AnimalPreview;
  veterinarian: UserPreview;
  clinic: ClinicPreview;
};
