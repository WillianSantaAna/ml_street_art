const pool = require("./connection");

module.exports.getAllImages = async () => {
  try {
    const sql = "SELECT * FROM images";
    let result = await pool.query(sql);

    result = result.rows;

    return { status: 200, result };
  } catch (error) {
    return { status: 200, result: error };
  }
};
