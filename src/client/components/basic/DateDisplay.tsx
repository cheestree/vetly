import { format } from "date-fns";
import CustomText from "./custom/CustomText";

export default function DateRangeDisplay({
  start,
  end,
}: {
  start?: Date;
  end?: Date;
}) {
  if (!start && !end) return null;
  if (start && end && start.getTime() === end.getTime()) {
    return <CustomText text={format(start, "MMM d, yyyy")} />;
  }
  if (start && end) {
    return (
      <CustomText
        text={`${format(start, "MMM d, yyyy")} - ${format(end, "MMM d, yyyy")}`}
      />
    );
  }
  if (start) {
    return <CustomText text={`From ${format(start, "MMM d, yyyy")}`} />;
  }
  if (end) {
    return <CustomText text={`Until ${format(end, "MMM d, yyyy")}`} />;
  }
  return null;
}
