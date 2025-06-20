import { RequestCreate } from "@/api/request/request.input";

type RequestCreateContentProps = {
  onCreate: (createdRequest: RequestCreate) => Promise<void>;
  loading?: boolean;
};

export default function RequestCreateContent({
  onCreate,
  loading,
}: RequestCreateContentProps) {
  return <></>;
}
