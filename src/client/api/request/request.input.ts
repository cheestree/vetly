import { QueryParams } from "../QueryParams";

type RequestCreate = {
  action: RequestAction;
  target: RequestTarget;
  justification: string;
  extraData: JSON;
};

type RequestUpdate = {
  decision: RequestStatus;
  justification: string;
};

type RequestQueryParams = QueryParams & {
  action?: string;
  target?: string;
  status?: string;
  submittedBefore?: string;
  submittedAfter?: string;
};

type UserRequestQueryParams = RequestQueryParams & {
  userId?: string;
  username?: string;
};

enum RequestStatus {
  PENDING = "PENDING",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
}

enum RequestAction {
  CREATE = "CREATE",
  DELETE = "DELETE",
  UPDATE = "UPDATE",
}

enum RequestTarget {
  PET = "PET",
  ROLE = "ROLE",
  USER = "USER",
  CLINIC = "CLINIC",
  CLINIC_MEMBERSHIP = "CLINIC_MEMBERSHIP",
}

export {
  RequestAction,
  RequestCreate,
  RequestQueryParams,
  RequestStatus,
  RequestTarget,
  RequestUpdate,
  UserRequestQueryParams,
};
