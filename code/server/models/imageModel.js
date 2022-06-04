const pool = require("./connection");

module.exports.getAllImages = async () => {
  try {
    const sql = "SELECT * FROM images";
    let result = await pool.query(sql);

    result = result.rows;

    return { status: 200, result };
  } catch (error) {
    return { status: 500, result: error };
  }
};

module.exports.addImage = async (sta_id, usr_id, url) => {
  try {
    if (isNaN(sta_id)) {
      return { status: 400, result: { message: "Invalid Street Art ID" } };
    } else if (isNaN(usr_id)) {
      return { status: 400, result: { message: "Invalid user ID" } };
    } else if (url == '') {
      return { status: 400, result: { message: "URL can't be empty" } };
    }
    
    const sql = "insert into images (img_sta_id, img_usr_id, img_url) values ($1, $2, $3) returning img_url";
    const result = await pool.query(sql, [sta_id, usr_id, url]);

    if (result.rows.length > 0) {
      return { status: 200, result: result.rows[0] };
    } else {
      return { status: 400, result: { message: "Bad request" } };
    }
  } catch (error) {
    return { status: 500, result: error };
  }
};
