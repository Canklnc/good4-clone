import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import PrivacyPolicy from "./PrivacyPolicy";
import "./styles.css";

const normalizedPath = window.location.pathname.replace(/\/$/, "") || "/";
const isPrivacyPage = normalizedPath === "/gizlilik" || normalizedPath === "/privacy";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    {isPrivacyPage ? <PrivacyPolicy /> : <App />}
  </StrictMode>,
);
