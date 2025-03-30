import { useState } from "react";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import classes from "./VerifyEmail.module.scss";

const VerifyEmail = () => {
  const [errorMessages, setErrorMessages] = useState("");
  const [message, setMessage] = useState("");
  return (
    <AuthLayout className={classes.root}>
      <Box>
        <form>
          <h1>Verify Your Email</h1>
          <p>
            We have sent a verification link to your email. Please verify your
            email to continue.
          </p>
          <Input name="code" type="text" label="Verification Code" />
          {message && <p style={{ color: "green" }}>{message}</p>}
          {errorMessages && <p style={{ color: "red" }}>{errorMessages}</p>}
          <Button type="submit">Verify</Button>
          <Button outline type="button">
            Resend Verification Email
            </Button>
        </form>
      </Box>
    </AuthLayout>
  );
};

export default VerifyEmail;
