const path = require("path");
const download = require("image-downloader");

const downloadFile = async (url, folderName) => {
  try {
    const fileName = url.match(/\d{2,}/g)[0] + ".jpg";
    const localPath = path.resolve(folderName, fileName);

    const options = {
      url,
      dest: localPath,
    };

    await download.image(options);
  } catch (error) {
    console.log("error: ", error);
  }
};

module.exports = downloadFile;
