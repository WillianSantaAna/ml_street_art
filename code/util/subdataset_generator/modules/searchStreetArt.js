const path = require("path");
const fs = require("fs-extra");

const searchStreetArt = (datasetPath, subdatasetPath) => {
  let artistList = [];

  if (!fs.existsSync(subdatasetPath)) {
    fs.mkdirSync(subdatasetPath);
  }

  const folderList = fs.readdirSync(datasetPath);

  for (let folder of folderList) {
    const dataPath = path.resolve(datasetPath, folder, "data.json");
    const rawData = fs.readFileSync(dataPath);
    const data = JSON.parse(rawData);

    if (data.artists && data.artists.length === 1) {
      for (let artist of data.artists) {
        const index = artistList.findIndex((x) => x.name === artist);

        if (index < 0) {
          artistList.push({
            name: artist,
            artsCount: 1,
            imgsCount: data.images.length,
            artList: [folder],
          });
        } else {
          artistList[index].artsCount++;
          artistList[index].imgsCount += data.images.length;
          artistList[index].artList.push(folder);
        }
      }
    }
  }

  artistList = artistList.filter((artist) => {
    return !(
      artist.name === "Autor desconhecido" || artist.name === "Desconhecido"
    );
  });

  artistList.sort((a, b) => b.artsCount - a.artsCount);

  fs.writeFileSync(
    path.resolve(subdatasetPath, "artistList.json"),
    JSON.stringify(artistList.slice(0, 10), null, 2)
  );
};

module.exports = searchStreetArt;
