import { useRouter } from "next/router";
import { useEffect, type ReactNode } from "react";
import { hasRole, useAuth } from "../lib/auth";
import type { Role } from "../lib/types";

export default function ProtectedRoute({ children, roles }: { children: ReactNode; roles?: Role | Role[] }) {
  const router = useRouter();
  const { user, loading } = useAuth();
  useEffect(() => {
    if (!loading && (!user || !hasRole(user, roles))) router.replace(user ? "/dashboard" : "/");
  }, [loading, user, roles, router]);
  if (loading || !user || !hasRole(user, roles)) return <div className="loading">Checking access…</div>;
  return <>{children}</>;
}
