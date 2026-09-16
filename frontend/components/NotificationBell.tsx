import Link from "next/link";
import { useEffect, useState } from "react";
import { apiFetch } from "../lib/api";
import type { Notification } from "../lib/types";
import { useAuth } from "../lib/auth";

export default function NotificationBell() {
  const { user } = useAuth();
  const [items, setItems] = useState<Notification[]>([]);
  useEffect(() => {
    if (!user) return;
    let active = true;
    const poll = async () => { try { const next = await apiFetch<Notification[]>(`/api/notifications?user=${encodeURIComponent(String(user.id))}`); if (active) setItems(next || []); } catch { /* endpoint is optional until backend notifications are enabled */ } };
    poll(); const timer = window.setInterval(poll, 30000);
    return () => { active = false; window.clearInterval(timer); };
  }, [user]);
  const unread = items.filter((item) => !item.read).length;
  return <Link href="/notifications" className="notification-bell" aria-label={`${unread} unread notifications`}>♢{unread > 0 && <b>{unread > 9 ? "9+" : unread}</b>}</Link>;
}
