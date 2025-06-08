type ClinicPreview = {
  id: string;
  name: string;
  address: string;
  phone: string;
  hours: OpeningHour[];
  imageUrl: string;
  services: ServiceType[];
};
