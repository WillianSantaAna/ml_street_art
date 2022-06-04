const express = require("express");
const userModel = require("../models/userModel");
const router = express.Router();

/* GET users listing. */
router.get("/", async (req, res) => {
  const { status, result } = await userModel.getAllUsers();

  res.status(status).send(result);
});

router.get("/:id/images", async (req, res) => {
  const id = req.params.id;
  const { status, result } = await userModel.getUserImages(id);

  res.status(status).send(result);
});

router.post("/login", async (req, res) => {
  const user = req.body;
  const { status, result } = await userModel.login(user);

  res.status(status).send(result);
});

router.post("/register", async (req, res) => {
  const user = req.body;
  const { status, result } = await userModel.register(user);

  res.status(status).send(result);
});

module.exports = router;
