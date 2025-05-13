type CheckupPreview = {
  id: number;
  description: string;
  dateTime: string;
  status: string;
  animal: AnimalPreview;
  veterinarian: UserPreview;
  clinic: ClinicPreview;
};
