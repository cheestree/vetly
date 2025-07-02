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

const actionOptions = [
  { label: "Any Action", key: "", value: undefined },
  ...Object.entries(RequestAction).map(([key, value]) => ({
    label: key.charAt(0) + key.slice(1).toLowerCase(),
    key: value,
    value: value,
  })),
];

const targetOptions = [
  { label: "Any Target", key: "", value: undefined },
  ...Object.entries(RequestTarget).map(([key, value]) => ({
    label: key.charAt(0) + key.slice(1).toLowerCase(),
    key: value,
    value: value,
  })),
];
const statusOptions = [
  { label: "Any Status", key: "", value: undefined },
  ...Object.entries(RequestStatus).map(([key, value]) => ({
    label: key.charAt(0) + key.slice(1).toLowerCase(),
    key: value,
    value: value,
  })),
];

export {
  actionOptions,
  RequestAction,
  RequestCreate,
  RequestQueryParams,
  RequestStatus,
  RequestTarget,
  RequestUpdate,
  statusOptions,
  targetOptions,
  UserRequestQueryParams,
};
