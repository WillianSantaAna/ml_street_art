const path = require("path");
const fs = require("fs-extra");

const { searchStreetArt, copyStreetArt, formatDataset } = require("./modules");

const rootPath = path.resolve("../../../");
const datasetPath = path.resolve(rootPath, "dataset");
const subdatasetPath = path.resolve(rootPath, "subdataset");

searchStreetArt(datasetPath, subdatasetPath);
copyStreetArt(datasetPath, subdatasetPath);
formatDataset(subdatasetPath);
