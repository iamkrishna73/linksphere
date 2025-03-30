import classes from "./Feed.module.scss";
const Feed = () => {
  return (
    <div className={classes.root}>
      <header className={classes.header}>
        <div>Hello mikedane@gmail.com</div>
        <span></span>
        <button>Logout</button>
      </header>
      <main className={classes.content}>
        <div className={classes.left}></div>
        <div className={classes.center}>
          <div className={classes.posting}></div>
          <div className={classes.feed}></div>
        </div>
        <div className={classes.right}></div>
      </main>
    </div>
  );
};

export default Feed;
