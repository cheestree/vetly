type UserAuthenticated = {
  token: string;
  user: UserInformation;
};

type UserInformation = {
  publicId: string;
  name: string;
  email: string;
  imageUrl: string;
  roles: string[];
};

type UserPreview = {
  id: string;
  name: string;
  imageUrl: string;
};

enum Role {
  VETERINARIAN,
  ADMIN,
}
