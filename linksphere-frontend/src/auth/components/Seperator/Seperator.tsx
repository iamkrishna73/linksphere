import { ReactNode } from "react";
import classes from "./Seperator.module.scss";
const Seperator = ({ children }: { children?: ReactNode }) => {
  return <div className={classes.separator}>{children}</div>;
};

export default Seperator;
