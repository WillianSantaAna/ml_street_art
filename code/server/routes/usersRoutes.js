const express = require("express");
const userModel = require("../models/userModel");
const router = express.Router();

/* GET users listing. */
router.get("/", async (req, res) => {
  const { status, result } = await userModel.getAllUsers();

  res.status(status).json(result);
});

module.exports = router;
