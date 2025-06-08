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
