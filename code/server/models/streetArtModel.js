const pool = require("./connection");

module.exports.getAllStreetArts = async () => {
  try {
    const sql = "SELECT * FROM street_arts";
    let result = await pool.query(sql);

    result = result.rows;

    return { status: 200, result };
  } catch (error) {
    return { status: 200, result: error };
  }
};
