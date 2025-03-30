import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import classes from "./ResetPassword.module.scss";
const ResetPassword = () => {
  const navigate = useNavigate();
  const [emailSent, setEmailSent] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  return (
    <AuthLayout className={classes.root}>
      <Box>
        <h1>Reset Password</h1>
        {!emailSent ? (
          <form>
            <p>
              We’ll send a verification code to this email or phone number if it
              matches an existing LinkedSphere account.
            </p>

            <Input name="email" type="email" label="email" />
            <p style={{ color: "red" }}>{errorMessage}</p>
            <Button type="submit">Next</Button>
            <Button type="button" outline onClick={() => navigate("/login")}>
              Back
            </Button>
          </form>
        ) : (
          <form>
            <p>
              Enter the verification code we sent to your email and your new
              password.
            </p>

            <Input
              type="text"
              label="verification token"
              key="code"
              name="code"
            />
            <Input
              name="password"
              type="password"
              label="password"
              key="password"
              id="password"
            />
            <p style={{ color: "red" }}>{errorMessage}</p>
            <Button type="submit">Next</Button>
            <Button type="button" outline onClick={() => navigate("/login")}>
              Back
            </Button>
          </form>
        )}
      </Box>
    </AuthLayout>
  );
};

export default ResetPassword;
