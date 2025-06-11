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
  joinedAt: string;
};

type UserPreview = {
  id: string;
  name: string;
  email: string;
  imageUrl: string;
};

enum Role {
  VETERINARIAN,
  ADMIN,
}
