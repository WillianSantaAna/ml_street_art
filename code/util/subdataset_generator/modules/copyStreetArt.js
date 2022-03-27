const _ = require("lodash");
const path = require("path");
const fs = require("fs-extra");

const copyStreetArt = (datasetPath, subdatasetPath) => {
  const artistListPath = path.resolve(subdatasetPath, "artistList.json");
  const artistListRaw = fs.readFileSync(artistListPath);
  const artistList = JSON.parse(artistListRaw);

  for (let artist of artistList) {
    const artistName = _.snakeCase(artist.name);
    const artistPath = path.resolve(subdatasetPath, artistName);

    if (!fs.existsSync(artistPath)) {
      fs.mkdirSync(artistPath);
    }

    for (let art of artist.artList) {
      const imagesPath = path.resolve(datasetPath, art, "images");
      const imagesList = fs.readdirSync(imagesPath);

      for (let image of imagesList) {
        const srcPath = path.resolve(imagesPath, image);
        const destPath = path.resolve(artistPath, image);

        fs.copySync(srcPath, destPath);
      }
    }
  }
};

module.exports = copyStreetArt;
