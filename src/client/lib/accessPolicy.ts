import { Role } from "@/api/user/user.output";
import { Route, protectedRoutes } from "@/lib/types";

function hasRole(roles?: Role[], ...allowedRoles: Role[]) {
  if (!roles || allowedRoles.length === 0) return false;
  return allowedRoles.some((role) => roles.includes(role));
}

function canAccessRoles(roles: Role[] | undefined, requiredRoles: Role[]) {
  if (requiredRoles.length === 0) return true;
  return hasRole(roles, ...requiredRoles);
}

function isDynamicSegment(segment: string) {
  return segment.startsWith("[") && segment.endsWith("]");
}

function routePathMatchesSegments(routePath: string, segments: string[]) {
  const routeSegments = routePath.split("/").filter(Boolean);

  return (
    routeSegments.length === segments.length &&
    routeSegments.every(
      (routeSegment, index) =>
        isDynamicSegment(routeSegment) || routeSegment === segments[index],
    )
  );
}

function checkRouteAccess(segments: string[], roles: Role[] = []) {
  const cleanSegments = segments.filter((segment) => !segment.startsWith("("));
  const matchedRoute = protectedRoutes.find((route) =>
    routePathMatchesSegments(route.path, cleanSegments),
  );

  if (!matchedRoute) return true;
  return canAccessRoles(roles, matchedRoute.roles);
}

function canViewRoute(route: Route, authenticated: boolean, roles: Role[] = []) {
  if (route.restrictedWhenAuthenticated && authenticated) return false;
  if (route.authenticated && !authenticated) return false;
  return canAccessRoles(roles, route.roles);
}

function filterRoutesByAccess(
  routes: Route[],
  authenticated: boolean,
  roles: Role[] = [],
) {
  return routes.filter((route) => canViewRoute(route, authenticated, roles));
}

export {
  canAccessRoles,
  canViewRoute,
  checkRouteAccess,
  filterRoutesByAccess,
  hasRole,
};
