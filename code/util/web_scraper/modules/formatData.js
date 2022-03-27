// {
//   "id": "2149",
//   "details": [
//     {
//       "Artista(s)": "Mosaik, Mr Dheo"
//     },
//     {
//       "Projeto": "Lisbon Week 2017"
//     },
//     {
//       "Ano": "2010"
//     },
//     {
//       "Créditos fotográficos": "© CML | DMC | DPC | Melanie Branco 2010"
//     },
//     {
//       "Local": "Travessa de Santo Antão"
//     },
//     {
//       "Estado": "Desconhecido"
//     }
//   ],
//   "images": [
//     "http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=4200",
//     "http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=4201"
//   ],
//   "coords": [
//     "38.715982",
//     "-9.140784"
//   ]
// }

// {
//   "id": "2149",
//   "artists": [
//     "Mosaik",
//     "Mr Dheo"
//   ],
//   "project": "Lisbon Week 2017",
//   "year": "2010",
//   "photoCredits": "© CML | DMC | DPC | Melanie Branco 2010",
//   "address": "Travessa de Santo Antão",
//   "state": "Desconhecido",
//   "coords": [
//     "38.715982",
//     "-9.140784"
//   ],
//   "images": [
//     "http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=4200",
//     "http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=4201"
//   ]
// }

const formatData = (data) => {
  let tempData = {
    id: data.id,
  };

  for (let detail of data.details) {
    if (Object.keys(detail)[0] === "Artista(s)") {
      tempData.artists = Object.values(detail)[0].split(", ");
    }

    if (Object.keys(detail)[0] === "Projeto") {
      tempData.project = Object.values(detail)[0];
    }

    if (Object.keys(detail)[0] === "Ano") {
      tempData.year = Object.values(detail)[0];
    }

    if (Object.keys(detail)[0] === "Créditos fotográficos") {
      tempData.photoCredits = Object.values(detail)[0];
    }

    if (Object.keys(detail)[0] === "Local") {
      tempData.address = Object.values(detail)[0];
    }

    if (Object.keys(detail)[0] === "Estado") {
      tempData.state = Object.values(detail)[0];
    }
  }

  tempData.coords = data.coords;
  tempData.images = data.images;

  return tempData;
};

module.exports = formatData;
