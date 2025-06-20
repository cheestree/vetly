import { ClinicMembershipPreview } from "../clinic/clinic.output";

type UserAuthenticated = {
  token: string;
  user: UserInformation;
};

type UserInformation = {
  publicId: string;
  name: string;
  email: string;
  imageUrl: string;
  roles: Role[];
  clinics: ClinicMembershipPreview[];
  joinedAt: string;
};

type UserPreview = {
  id: string;
  name: string;
  email: string;
  imageUrl: string;
};

type UserLink = {
  id: string;
  name: string;
  imageUrl?: string;
};

enum Role {
  VETERINARIAN,
  ADMIN,
}

export { Role, UserAuthenticated, UserInformation, UserLink, UserPreview };
