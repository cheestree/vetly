function hasRole(roles: string[], ...allowedRoles: string[]) {
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

export { dropMilliseconds, formatOpeningHours, hasRole, splitDateTime };
