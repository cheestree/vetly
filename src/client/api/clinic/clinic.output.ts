type ClinicPreview = {
  id: string;
  name: string;
  address: string;
  phone: string;
  openingHours: OpeningHour[];
  imageUrl?: string;
  services: ServiceType[];
};

type ClinicInformation = {
  id: string;
  name: string;
  address: string;
  lat: number;
  lng: number;
  phone: string;
  email: string;
  openingHours: OpeningHour[];
  imageUrl?: string;
  services: ServiceType[];
  owner?: UserPreview;
};

type OpeningHour = {
  weekday: number;
  opensAt: string;
  closesAt: string;
};

enum ServiceType {
  EMERGENCY,
  VACCINATION,
  CHECKUP,
  SURGERY,
}
