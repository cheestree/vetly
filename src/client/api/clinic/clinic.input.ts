type ClinicCreate = {
  name: string;
  nif: string;
  address: string;
  lng: number;
  lat: number;
  phone: string;
  email: string;
  services: ServiceType[];
  openingHours: OpeningHourModel[];
  imageUrl?: string;
  ownerId?: number;
};

type ClinicUpdate = {
  name?: string;
  nif?: string;
  address?: string;
  lng?: number;
  lat?: number;
  phone?: string;
  email?: string;
  services?: ServiceType[];
  openingHours?: OpeningHourModel[];
  imageUrl?: string;
  ownerId?: number;
};

type OpeningHourModel = {
  weekDay: number;
  opensAt: string;
  closesAt: string;
};

type ClinicQueryParams = QueryParams & {
  name?: string;
  lat?: number;
  lng?: number;
};
