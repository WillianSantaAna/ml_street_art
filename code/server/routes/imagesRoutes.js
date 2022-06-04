const express = require("express");
const imageModel = require("../models/imageModel");
const router = express.Router();
const cloudinary = require("cloudinary").v2;

router.get("/", async (req, res) => {
  const { status, result } = await imageModel.getAllImages();

  res.status(status).send(result);
});

router.post("/upload/street_art/:sta_id/user/:usr_id", async (req, res) => {
  const { sta_id, usr_id } = req.params;
  const upload = await cloudinary.uploader.upload(
    req.files.image.tempFilePath,
    {
      use_filename: true,
      folder: "street-art-images",
    }
  );

  const { status, result } = await imageModel.addImage(sta_id, usr_id, upload.secure_url);

  res.status(status).send(result);
});


module.exports = router;
