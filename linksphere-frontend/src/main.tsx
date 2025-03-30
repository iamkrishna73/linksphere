import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Login from "./auth/pages/Login/Login";
import ResetPassword from "./auth/pages/ResetPassword/ResetPassword";
import Signup from "./auth/pages/Signup/Signup";
import VerifyEmail from "./auth/pages/VerifyEmail/VerifyEmail";
import { AuthContextProvider } from "./context/AuthContextProvider";
import Feed from "./feed/Feed";
import "./index.scss";

const router = createBrowserRouter([
  {
    element: <AuthContextProvider />,
    children: [
      {
        path: "/",
        element: <Feed />,
      },
      {
        path: "/login",
        element: <Login />,
      },
      {
        path: "/signup",
        element: <Signup />,
      },
      {
        path: "/password-reset-request",
        element: <ResetPassword />,
      },
      {
        path: "/verify-email",
        element: <VerifyEmail />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
