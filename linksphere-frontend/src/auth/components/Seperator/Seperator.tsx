import { ReactNode } from "react";
import classes from "./Seperator.module.scss";
const Seperator = ({ children }: { children?: ReactNode }) => {
  return <div className={classes.seperator}>{children}</div>;
};

export default Seperator;
