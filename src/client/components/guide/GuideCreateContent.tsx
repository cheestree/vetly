import { GuideCreate } from "@/api/guide/guide.input";

type GuideCreateContentProps = {
  onCreate: (createdGuide: GuideCreate) => Promise<void>;
  loading?: boolean;
};

export default function GuideCreateContent({
  onCreate,
  loading,
}: GuideCreateContentProps) {
  return <></>;
}
