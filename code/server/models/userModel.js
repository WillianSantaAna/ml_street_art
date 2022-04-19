const pool = require("./connection");

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
