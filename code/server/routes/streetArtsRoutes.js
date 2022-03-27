const express = require("express");
const streetArtModel = require("../models/streetArtModel");
const router = express.Router();

router.get("/", async (req, res) => {
  const { status, result } = await streetArtModel.getAllStreetArts();

  res.status(status).json(result);
});

module.exports = router;
