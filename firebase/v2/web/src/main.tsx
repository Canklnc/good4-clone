import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import MembershipAgreement from "./MembershipAgreement";
import PrivacyPolicy from "./PrivacyPolicy";
import "./styles.css";

const normalizedPath = window.location.pathname.replace(/\/$/, "") || "/";
const isPrivacyPage = normalizedPath === "/gizlilik" || normalizedPath === "/privacy";
const isAgreementPage = normalizedPath === "/uyelik-sozlesmesi" || normalizedPath === "/terms";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    {isAgreementPage ? <MembershipAgreement /> : isPrivacyPage ? <PrivacyPolicy /> : <App />}
  </StrictMode>,
);
