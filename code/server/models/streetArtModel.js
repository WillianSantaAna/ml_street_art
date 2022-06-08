const pool = require("./connection");
const tf = require('@tensorflow/tfjs-node');
const Jimp = require('jimp');
const labels = ["Bordalo 2", "LS", "Mar", "Pariz One", "Uniao Artistica"];

module.exports.getAllStreetArts = async () => {
  try {
    const sql = `select sta_id, sta_usr_id, sta_artist, sta_year, sta_photo_credits, sta_address,
      sta_coords, sta_status, sta_published, sta_active, img_url from (
        select sta.*, img_url, rank() over (partition by img_sta_id order by img_id) img_rank
        from street_arts sta inner join images i on sta_id = img_sta_id
    ) as t where t.img_rank = 1`;
    const result = await pool.query(sql);

    return { status: 200, result: result.rows };
  } catch (error) {
    console.log(error);
    return { status: 500, result: error };
  }
};

module.exports.getStreetArtImages = async (id) => {
  try {
    const sql = `select * from images where img_sta_id = $1`;
    const result = await pool.query(sql, [id]);

    return { status: 200, result: result.rows };
  } catch (error) {
    console.log(error);
    return { status: 500, result: error };
  }
};

module.exports.predict = async (tempImgPath) => {
  try {
    const image = await Jimp.read(tempImgPath);
    const model = await tf.loadLayersModel("file://models/street_art_tfjs_model/model.json");

    image.cover(150, 150, Jimp.HORIZONTAL_ALIGN_CENTER | Jimp.VERTICAL_ALIGN_MIDDLE);

    const channels = 3;
    let values = new Float32Array(150 * 150 * channels);

    let i = 0;
    image.scan(0, 0, image.bitmap.width, image.bitmap.height, (x, y, idx) => {
      const pixel = Jimp.intToRGBA(image.getPixelColor(x, y));
      pixel.r = pixel.r / 127.0 - 1;
      pixel.g = pixel.g / 127.0 - 1;
      pixel.b = pixel.b / 127.0 - 1;
      pixel.a = pixel.a / 127.0 - 1;
      values[i * channels + 0] = pixel.r;
      values[i * channels + 1] = pixel.g;
      values[i * channels + 2] = pixel.b;
      i++;
    });

    const outShape = [150, 150, channels];
    let img_tensor = tf.tensor3d(values, outShape, 'float32');
    img_tensor = img_tensor.expandDims(0);

    const predictions = await model.predict(img_tensor).dataSync();

    const resultPredictions = labels.map((v, k) => {
      return {
        author: v,
        prediction: predictions[k]
      };
    });

    return { status: 200, result: resultPredictions };
  } catch (error) {
    console.log(error);
    return { status: 500, result: error };
  }
}

module.exports.addStreetArt = async (streetArt) => {
  try {
    const { usr_id, artist, project, year, credits, address, coords, status } = streetArt;

    for (const [key, value] of Object.entries(streetArt)) {
      if (key === 'usr_id') {
        if (isNaN(value)) {
          return { status: 400, result: { message: "Invalid user ID" } };
        }
      } else {
        if (value == '' || value == undefined) {
          return { status: 400, result: { message: `Street Art ${key} can't be empty` } };
        }
      }
    }

    const sql = `insert into street_arts (sta_usr_id, sta_artist, sta_project, sta_year, sta_photo_credits,
      sta_address, sta_coords, sta_status) values ($1, $2, $3, $4, $5, $6, $7, $8) returning sta_id`;

    const result = await pool.query(sql, [usr_id, artist, project, year, credits, address, coords, status]);

    if (result.rows.length > 0) {
      return { status: 200, result: result.rows[0] };
    } else {
      return { status: 400, result: { message: "Bad request" } };
    }
  } catch (error) {
    console.log(error);
    return { status: 500, result: error };
  }
}
