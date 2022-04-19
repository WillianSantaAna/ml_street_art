const pool = require("./connection");
const bcrypt = require("bcrypt");
const saltRounds = 10;

module.exports.getAllUsers = async () => {
  try {
    const sql = "SELECT * FROM users";
    let result = await pool.query(sql);

    result = result.rows;

    return { status: 200, result };
  } catch (error) {
    console.log(error)
    return { status: 500, result: error };
  }
};

module.exports.register = async (user) => {
  const { name, email, password, type } = user;

  if (typeof user !== "object") {
    return { status: 400, result: { msg: "Malformed data" } };
  }

  if (typeof name !== "string" || name.length < 3) {
    return { status: 400, result: { msg: "Invalid name" } };
  }

  if (typeof email !== "string" || email.length < 3) {
    return { status: 400, result: { msg: "Invalid email" } };
  }

  if (typeof password !== "string" || password.length < 6) {
    return { status: 400, result: { msg: "Invalid password" } };
  }

  if (typeof type !== "string" || email.length < 3) {
    return { status: 400, result: { msg: "Invalid type" } };
  }

  try {
    const hash = await bcrypt.hash(password, saltRounds);

    const sql = `insert into users (usr_name, usr_email, usr_password, usr_type)
      values ($1, $2, $3, $4) returning usr_id, usr_name`;
    let result = await pool.query(sql, [name, email, hash, type]);

    result = result.rows[0];

    return { status: 200, result };
  } catch (error) {
    console.log(error)
    return { status: 500, result: error };
  }
};

module.exports.login = async (user) => {
  try {
    const { email, password } = user;
    const sql = `select * from users where usr_email = $1`;

    let result = await pool.query(sql, [email]);

    if (result.rowCount <= 0) {
      return { status: 400, result: { msg: "Wrong email" } };
    }

    result = result.rows[0];

    const match = await bcrypt.compare(password, result.usr_password);

    if (!match) {
      return { status: 400, result: { msg: "Wrong password" } };
    }

    delete result.usr_password;

    return { status: 200, result };
  } catch (error) {
    console.log(error)
    return { status: 500, result: error };
  }
};