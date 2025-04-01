import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../../components/Button/Button";
import Input from "../../../components/Input/Input";
import AuthLayout from "../../components/AuthLayout";
import Box from "../../components/Box/Box";
import classes from "./VerifyEmail.module.scss";

const VerifyEmail = () => {
  const [errorMessages, setErrorMessages] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const nivagate = useNavigate();

  const hanadleValidateEmail = async (code: string) => {
    setErrorMessages("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/validate-email-verification-token?token=${code}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setMessage("");
        nivagate("/");
      }
      const { message } = await response.json();
      setErrorMessages(message);
    } catch (error) {
      console.log(error);
      setErrorMessages("An unknown error occurred.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleSentEmailVerificationToken = async () => {
    setErrorMessages("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/send-email-verification-token`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setErrorMessages("");
        setMessage("Verification token sent to email successfully.");
      } else {
        const { message } = await response.json();
        setErrorMessages(message);
      }
    } catch (error) {
      console.log(error);
      setErrorMessages("An unknown error occurred.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout className={classes.root}>
      <Box>
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            setIsLoading(true);
            const code = e.currentTarget.code.value;
            await hanadleValidateEmail(code);
            // e.currentTarget.reset();
            setIsLoading(false);
          }}
        >
          <h1>Verify Your Email</h1>
          <p>
            We have sent a verification link to your email. Please verify your
            email to continue.
          </p>
          <Input name="code" type="text" label="Verification Code" id="code" />
          {message && <p style={{ color: "green" }}>{message}</p>}
          {errorMessages && <p style={{ color: "red" }}>{errorMessages}</p>}
          <Button type="submit">Verify</Button>
          <Button
            outline
            type="button"
            onClick={() => {
              handleSentEmailVerificationToken();
            }}
          >
            Resend Verification Email
          </Button>
        </form>
      </Box>
    </AuthLayout>
  );
};

export default VerifyEmail;
