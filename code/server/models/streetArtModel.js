const pool = require("./connection");
const tf = require('@tensorflow/tfjs-node');
const Jimp = require('jimp');

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

    return { status: 200, result: predictions };
  } catch (error) {
    console.log(error)
    return { status: 500, result: error };
  }
}