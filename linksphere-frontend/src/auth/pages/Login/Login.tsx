import { FormEvent, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import { useAuth } from "../../../context/AuthContextProvider";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import Seperator from "../../components/Seperator/Seperator";
import classes from "./Login.module.scss";

const Login = () => {
  const [errorMessages, setErrorMessages] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;

    console.log("login", email, password);
    try {
      await login(email, password);
      const destination = location.state?.from || "/";

      navigate(destination);
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessages(error.message);
      } else {
        setErrorMessages("An unknown error occurred.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout className={classes.root}>
      <Box>
        <h1>Login</h1>
        <p>Make the most of your professional life</p>
        <form onSubmit={handleLogin}>
          <Input
            label="Email"
            type="email"
            id="email"
            onFocus={() => setErrorMessages("")}
          />
          <Input
            label="Password"
            type="password"
            id="password"
            onFocus={() => setErrorMessages("")}
          />
          {errorMessages && <p className={classes.error}>{errorMessages}</p>}

          <Button type="submit" disabled={isLoading}>
            {isLoading ? "...": `Agree & Join`}
          </Button>
          <Link to="/password-reset-request">Forgot password?</Link>
        </form>

        <Seperator>Or</Seperator>

        <div className={classes.register}>
          New to LinkSphere <Link to="/signup">Join now</Link>
        </div>
      </Box>
    </AuthLayout>
  );
};

export default Login;
