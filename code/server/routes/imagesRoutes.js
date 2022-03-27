const express = require("express");
const imageModel = require("../models/imageModel");
const router = express.Router();

router.get("/", async (req, res) => {
  const { status, result } = await imageModel.getAllImages();

  res.status(status).json(result);
});

module.exports = router;
