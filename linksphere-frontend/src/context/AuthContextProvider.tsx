import { createContext, useContext, useEffect, useState } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";

interface User {
  id: string;
  email: string;
  emailVarified: boolean;
}

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthContextProvider() {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const location = useLocation();

  const isOnAuthPage =
    window.location.pathname === "/login" ||
    window.location.pathname === "/signup" ||
    window.location.pathname === "/password-reset-request" ||
    window.location.pathname === "/verify-email";

  const login = async (email: string, password: string) => {
    // Implement login logic here
    console.log("login1", email, password);
    console.log('backend url', import.meta.env.VITE_API_URL);

    const response = await fetch(
      import.meta.env.VITE_API_URL + "/api/v1/auth/login",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      }
    );
    if (response.ok) {
      const { token } = await response.json();
      localStorage.setItem("token", token);
    } else {
      const { message } = await response.json();
      throw new Error(message);
    }
  };

  const signup = async (email: string, password: string) => {
    // Implement signup logic here
    const response = await fetch(
      import.meta.env.VITE_API_URL + "/api/v1/auth/register",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      }
    );
    if (response.ok) {
      const { token } = await response.json();
      localStorage.setItem("token", token);
    } else {
      const { message } = await response.json();
      throw new Error(message);
    }
  };

  const logout = async () => {
    // Implement logout logic here
    localStorage.removeItem("token");
    setUser(null);
  };

  const fetchUser = async () => {
    try {
      const response = await fetch(
        import.meta.env.VITE_API_URL + "/api/v1/auth/user/me",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (!response.ok) {
        throw new Error("Failed to fetch user data");
      }
      const user = await response.json();
      setUser(user);
    } catch (error) {
      console.error("Error fetching user data:", error);
    } finally {
      // Optionally, you can add any cleanup or final actions here
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      return;
    }
    fetchUser();
  }, [user, location.pathname]);

  if (isLoading) {
    return <div>Loading...</div>;
  }
  if (!isLoading && !user && !isOnAuthPage) {
    return <Navigate to="/login" />;
  }
  if (user && isOnAuthPage && user.emailVarified) {
    return <Navigate to="/" />;
  }
  return (
    <AuthContext.Provider value={{ login, signup, logout, user }}>
      {user && !user.emailVarified && isOnAuthPage ? (
        <Navigate to="/verify-email" />
      ) : null}
      <Outlet />
    </AuthContext.Provider>
  );
}
