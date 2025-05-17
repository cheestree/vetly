const hasRole = (roles: string[], ...allowedRoles: string[]) => {
    return allowedRoles.some((role) => roles.includes(role));
};

export default { hasRole }