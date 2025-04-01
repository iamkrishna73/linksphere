import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import { useAuth } from "../../../context/AuthContextProvider";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import Seperator from "../../components/Seperator/Seperator";
import classes from "./Signup.module.scss";

const Signup = () => {
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const { signup } = useAuth();
  const navigate = useNavigate();

  const handleSignup = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;
    try {
      await signup(email, password);
      navigate("/");
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessages([error.message]);
      }
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <AuthLayout className={classes.root}>
      <Box>
        <h1>Signup</h1>
        <p>Make the most of your professional life</p>
        <form onSubmit={handleSignup}>
          <Input label="Email" type="email" id="email"/>
          <Input label="Password" type="password" id="password"/>
          {errorMessages && <p className={classes.error}>{errorMessages}</p>}
          <p className={classes.disclaimer}>
            By clicking Agree & Join, you agree to the LinkedSphere's{" "}
            <a href=""> User Agreement</a>,<a href="">Privacy Policy</a>,
            <a href=""> and Cookie Policy</a>.
          </p>
          <Button type="submit">Agree & Join</Button>
        </form>

        <Seperator>Or</Seperator>
        <div className={classes.register}>
          Already on LinkSphere <Link to="/login">Sign in</Link>
        </div>
      </Box>
    </AuthLayout>
  );
};

export default Signup;
