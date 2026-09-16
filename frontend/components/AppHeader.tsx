import Link from "next/link";
import { useRouter } from "next/router";
import { useAuth } from "../lib/auth";
import NotificationBell from "./NotificationBell";

export default function AppHeader() {
  const { user, logout } = useAuth();
  const router = useRouter();
  return <header className="app-header"><Link href="/dashboard" className="brand">Due<span>Diligence</span></Link>
    <nav><Link className={router.pathname === "/dashboard" ? "active" : ""} href="/dashboard">Dashboard</Link>
      <Link className={router.pathname.startsWith("/properties") ? "active" : ""} href="/properties">Properties</Link>
      <Link className={router.pathname.startsWith("/reports") ? "active" : ""} href="/reports">Reports</Link>
      {user?.role === "ADMINISTRATOR" && <Link href="/admin">Admin</Link>}
    </nav>
    <div className="header-actions"><NotificationBell /><Link href="/profile" className="avatar">{user?.fullName?.slice(0, 1).toUpperCase()}</Link>
      <button className="link-button" onClick={() => { logout(); router.push("/"); }}>Sign out</button></div>
  </header>;
}
