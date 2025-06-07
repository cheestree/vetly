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
    hours: date.getUTCHours(),
    minutes: date.getUTCMinutes(),
    seconds: date.getUTCSeconds(),
  };

  return { dateOnly, timeOnly };
}

export { splitDateTime, hasRole };
