const express = require("express");
const streetArtModel = require("../models/streetArtModel");
const router = express.Router();

router.get("/", async (req, res) => {
  const { status, result } = await streetArtModel.getAllStreetArts();

  res.status(status).send(result);
});

router.get("/:id/images", async (req, res) => {
  const id = req.params.id;
  const { status, result } = await streetArtModel.getStreetArtImages(id);

  res.status(status).send(result);
});

router.post("/predict", async (req, res) => {
  const img = req.files.image.tempFilePath;
  const { status, result } = await streetArtModel.predict(img);

  res.status(status).send(result);
});

router.post("/", async (req, res) => {
  const streetArt = req.body;
  const { status, result } = await streetArtModel.addStreetArt(streetArt);

  res.status(status).send(result);
});;

module.exports = router;
