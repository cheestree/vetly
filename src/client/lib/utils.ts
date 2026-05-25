import { OpeningHour } from "@/api/clinic/clinic.output";
import { Role, UserInformation } from "@/api/user/user.output";
export { checkRouteAccess, hasRole } from "@/lib/accessPolicy";

function splitDateTime(isoString: string) {
  const date = new Date(isoString);

  const dateOnly = [
    date.getFullYear(),
    (date.getMonth() + 1).toString().padStart(2, "0"),
    date.getDate().toString().padStart(2, "0"),
  ].join("-");

  const timeOnly = {
    hours: date.getUTCHours().toString().padStart(2, "0"),
    minutes: date.getUTCMinutes().toString().padStart(2, "0"),
    seconds: date.getUTCSeconds().toString().padStart(2, "0"),
  };

  return { dateOnly, timeOnly };
}

function formatOpeningHours(openingHours: OpeningHour[]): string {
  const days = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
  ];

  if (openingHours.length === 0) return "No opening hours listed";

  return openingHours
    .sort((a, b) => a.weekday - b.weekday)
    .map(({ weekday, opensAt, closesAt }) => {
      const dayName = days[weekday];
      return `${dayName}: ${opensAt} - ${closesAt}`;
    })
    .join("\n");
}

function dropMilliseconds(isoString: string) {
  return isoString.replace(/\.\d{3}Z$/, "Z");
}

function getGreeting(information: UserInformation | null): string {
  if (!information) return "Hello";
  if (information.roles?.includes(Role.ADMIN))
    return `Admin${information.name ? " " + information.name : ""}`;
  if (information.roles?.includes(Role.VETERINARIAN))
    return `Dr${information.name ? " " + information.name : ""}`;
  return `${information.name ? " " + information.name : ""}`;
}

export {
  dropMilliseconds,
  formatOpeningHours,
  getGreeting,
  splitDateTime,
};
