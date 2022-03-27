const puppeteer = require("puppeteer");

const collectData = async (url) => {
  const browser = await puppeteer.launch();

  try {
    const page = await browser.newPage();

    await page.goto(url);
    await page.waitForTimeout(7000);
    await page.click("a.jg-entry");
    await page.waitForTimeout(7000);

    const artData = await page.evaluate(() => {
      const artDetails = document.querySelector("#detalhe_obra");

      const id = document.querySelector("#objecto_id").getAttribute("value");

      const imagesUrl = Array.from(
        artDetails.querySelectorAll("#fotos img")
      ).map((img) => `http://gau.cm-lisboa.pt/${img.getAttribute("src")}`);

      const details = Array.from(
        artDetails.querySelectorAll("#descricao .detalhe_item")
      ).map((item) => {
        let text = item.innerHTML.replace(/<\/?b>/g, "").split(": ");

        return { [text[0]]: text[1] };
      });

      const mapUrl = Array.from(artDetails.querySelectorAll("#mapa_local a"))
        .map((item) => item.getAttribute("href"))
        .filter((item) => item.startsWith("https://maps.google.com/maps?"));

      const coords = mapUrl[0].match(/-?[0-9]+\.[0-9]+/g);

      return { id, details, images: imagesUrl, coords };
    });

    await browser.close();

    return artData;
  } catch (error) {
    await browser.close();
    console.log("***Failed to collect data***");
  }
};

module.exports = collectData;
