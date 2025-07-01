import { OpeningHour } from "@/api/clinic/clinic.output";
import { Role, UserInformation } from "@/api/user/user.output";
import { protectedRoutes } from "./types";

function hasRole(roles?: Role[], ...allowedRoles: Role[]) {
  if (!roles) return false;
  return allowedRoles.some((role) => roles.includes(role));
}

function splitDateTime(isoString: string) {
  const date = new Date(isoString);

  const dateOnly = new Date(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
  );

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

function checkRouteAccess(segments: string[], roles: Role[]): boolean {
  const cleanSegments = segments.filter((segment) => !segment.startsWith("("));

  const matchedRoute = protectedRoutes.find((route) => {
    const routeSegments = route.path.split("/").filter(Boolean);

    return (
      routeSegments.length === cleanSegments.length &&
      routeSegments.every(
        (segment, index) =>
          segment.startsWith(":") || segment === cleanSegments[index],
      )
    );
  });

  if (!matchedRoute) return true;

  return matchedRoute.roles.some((requiredRole) =>
    roles.includes(requiredRole),
  );
}

function getGreeting(information: UserInformation | null): string {
  if (!information) return "Hello";
  if (information.roles?.includes(Role.ADMIN))
    return `Hello Admin${information.name ? " " + information.name : ""}`;
  if (information.roles?.includes(Role.VETERINARIAN))
    return `Hello Dr${information.name ? " " + information.name : ""}`;
  return `Hello${information.name ? " " + information.name : ""}`;
}

export {
  checkRouteAccess,
  dropMilliseconds,
  formatOpeningHours,
  getGreeting,
  hasRole,
  splitDateTime,
};
