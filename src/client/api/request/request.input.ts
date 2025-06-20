import { QueryParams } from "../QueryParams";

type RequestCreate = {
  action: RequestAction;
  target: RequestTarget;
  justification: string;
  extraData: JSON;
  files: string[];
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
  userId?: number;
  username?: string;
};

enum RequestStatus {
  PENDING,
  APPROVED,
  REJECTED,
}

enum RequestAction {
  CREATE,
  DELETE,
  UPDATE,
}

enum RequestTarget {
  PET,
  ROLE,
  USER,
  CLINIC,
  CLINIC_MEMBERSHIP,
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
