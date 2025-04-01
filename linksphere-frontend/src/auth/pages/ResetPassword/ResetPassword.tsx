import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import classes from "./ResetPassword.module.scss";
const ResetPassword = () => {
  const navigate = useNavigate();
  const [emailSent, setEmailSent] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [email, setEmail] = useState("");

  const handleSendPasswordResetToken = async (email: string) => {
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/send-password-reset-token?email=${email}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (response.ok) {
        setEmailSent(true);
        setErrorMessage("");
      } else {
        const { message } = await response.json();
        setErrorMessage(message);
      }
    } catch (error) {
      console.log(error);
      setErrorMessage("An unknown error occurred.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetPaassword = async (
    code: string,
    password: string,
    email: string
  ) => {
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/reset-password?newPassword=${password}&token=${code}&email=${email}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      if (response.ok) {
        setErrorMessage("");
        navigate("/");
      }
      const { message } = await response.json();
      setErrorMessage(message);
    } catch (error) {
      console.log(error);
      setErrorMessage("An unknown error occurred.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout className={classes.root}>
      <Box>
        <h1>Reset Password</h1>
        {!emailSent ? (
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              setIsLoading(true);
              const email = e.currentTarget.email.value;
              await handleSendPasswordResetToken(email);
              setEmail(email);
              setIsLoading(false);
            }}
          >
            <p>
              We’ll send a verification code to this email or phone number if it
              matches an existing LinkedSphere account.
            </p>

            <Input name="email" type="email" label="email" id="email" />
            <p style={{ color: "red" }}>{errorMessage}</p>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "..." : "Next"}
            </Button>
            <Button
              type="button"
              outline
              disabled={isLoading}
              onClick={() => navigate("/login")}
            >
              Back
            </Button>
          </form>
        ) : (
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              setIsLoading(true);
              const code = e.currentTarget.code.value;
              const password = e.currentTarget.password.value;
              await handleResetPaassword(code, password, email);
              setIsLoading(false);
            }}
          >
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
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "..." : "Reset Password"}
            </Button>
            <Button
              outline
              type="button"
              onClick={() => {
                setEmailSent(false);
                setErrorMessage("");
              }}
              disabled={isLoading}
            >
              {isLoading ? "..." : "Back"}
            </Button>
          </form>
        )}
      </Box>
    </AuthLayout>
  );
};

export default ResetPassword;
