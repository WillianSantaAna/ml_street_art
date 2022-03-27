const _ = require("lodash");
const path = require("path");
const fs = require("fs-extra");
const validationSize = 0.1;
const testSize = 0.1;

const formatDataset = (subdatasetPath) => {
  const datasetPath = path.resolve(subdatasetPath, "dataset");
  const trainPath = path.resolve(datasetPath, "train");
  const validationPath = path.resolve(datasetPath, "validation");
  const testPath = path.resolve(datasetPath, "test");
  const artistListPath = path.resolve(subdatasetPath, "artistList.json");
  const artistListRaw = fs.readFileSync(artistListPath);
  const artistList = JSON.parse(artistListRaw).map((artist) =>
    _.snakeCase(artist.name)
  );

  if (!fs.existsSync(datasetPath)) {
    fs.mkdirSync(datasetPath);
    fs.mkdirSync(trainPath);
    fs.mkdirSync(validationPath);
    fs.mkdirSync(testPath);
  } else {
    throw Error(
      "You already have a dataset folder inside subdataset, move or rename it and try again."
    );
  }

  for (let artist of artistList) {
    const artistPath = path.resolve(subdatasetPath, artist);
    const imagesList = fs
      .readdirSync(artistPath)
      .sort(() => Math.random() - 0.5);
    const validationCount = Math.ceil(imagesList.length * validationSize);
    const testCount = Math.ceil(imagesList.length * testSize);
    const artistTrainPath = path.resolve(trainPath, artist);
    const artistValidationPath = path.resolve(validationPath, artist);
    const artistTestPath = path.resolve(testPath, artist);

    fs.mkdirSync(artistTrainPath);
    fs.mkdirSync(artistValidationPath);
    fs.mkdirSync(artistTestPath);

    for (let i in imagesList) {
      if (i < validationCount) {
        const src = path.resolve(artistPath, imagesList[i]);
        const dest = path.resolve(artistValidationPath, imagesList[i]);

        fs.copySync(src, dest);
      } else if (i < validationCount + testCount) {
        const src = path.resolve(artistPath, imagesList[i]);
        const dest = path.resolve(artistTestPath, imagesList[i]);

        fs.copySync(src, dest);
      } else {
        const src = path.resolve(artistPath, imagesList[i]);
        const dest = path.resolve(artistTrainPath, imagesList[i]);

        fs.copySync(src, dest);
      }
    }
  }
};

module.exports = formatDataset;
