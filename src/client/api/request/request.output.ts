import { UserInformation, UserPreview } from "../user/user.output";
import { RequestAction, RequestStatus, RequestTarget } from "./request.input";

type RequestPreview = {
  id: string;
  user: UserPreview;
  target: RequestTarget;
  action: RequestAction;
  status: RequestStatus;
  justification?: string;
  createdAt: string;
};

type RequestInformation = {
  id: string;
  user: UserInformation;
  target: RequestTarget;
  action: RequestAction;
  status: RequestStatus;
  justification?: string;
  files: string[];
  extraData: JSON;
  createdAt: string;
};

export { RequestInformation, RequestPreview };

