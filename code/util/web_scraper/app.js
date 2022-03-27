const fs = require("fs-extra");
const path = require("path");

const { formatData, collectData, downloadFile } = require("./modules");

const url = "http://gau.cm-lisboa.pt/galeria.html";

(async () => {
  const datasetFolderPath = path.resolve("../../../", "dataset");

  if (!fs.existsSync(datasetFolderPath)) {
    fs.mkdirSync(datasetFolderPath);
  }

  for (let i = 0; i < 1000; i++) {
    console.log("\nCollecting Data...");
    const startTime = new Date().getTime();
    const artData = await collectData(url);

    if (!artData) continue;

    const { id, images } = artData;
    const basePath = path.resolve(datasetFolderPath, id);

    if (!fs.existsSync(basePath)) {
      const imgPath = path.resolve(basePath, "images");
      const jsonPath = path.resolve(basePath, "data.json");

      fs.mkdirSync(basePath);
      fs.mkdirSync(imgPath);

      console.log(`Count: ${i}. Folder ${id} created!`);

      fs.writeFileSync(jsonPath, JSON.stringify(formatData(artData), null, 2));

      for (let imgUrl of images) {
        try {
          await downloadFile(imgUrl, imgPath);
        } catch (error) {
          console.log(error);
        }
      }
    }
    const endTime = new Date().getTime();

    const timeSpentSec = (endTime - startTime) / 1000;

    console.log(`Data Collected in ${timeSpentSec.toFixed(2)}`);
  }
})();
