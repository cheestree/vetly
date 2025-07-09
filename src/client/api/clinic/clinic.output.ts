import { UserLink, UserPreview } from "../user/user.output";

type ClinicPreview = {
  id: number;
  name: string;
  address: string;
  phone: string;
  openingHours: OpeningHour[];
  imageUrl?: string;
  services: ServiceType[];
};

type ClinicInformation = {
  id: number;
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

type ClinicLink = {
  id: number;
  name: string;
  imageUrl?: string;
};

type ClinicMembershipPreview = {
  user: UserLink;
  clinic: ClinicLink;
  joinedAt: string;
  leftInt?: string;
};

type OpeningHour = {
  weekday: number;
  opensAt: string;
  closesAt: string;
};

enum ServiceType {
  EMERGENCY = "EMERGENCY",
  VACCINATION = "VACCINATION",
  CHECKUP = "CHECKUP",
  SURGERY = "SURGERY",
}

export {
  ClinicInformation,
  ClinicLink,
  ClinicMembershipPreview,
  ClinicPreview,
  OpeningHour,
  ServiceType,
};
