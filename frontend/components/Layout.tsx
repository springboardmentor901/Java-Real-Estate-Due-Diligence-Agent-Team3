import type { ReactNode } from "react";
import AppHeader from "./AppHeader";
export default function Layout({ children }: { children: ReactNode }) { return <><AppHeader /><main className="page-shell">{children}</main></>; }
