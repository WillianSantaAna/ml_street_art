window.onload = async () => {
    $("#image").on("change", async (e) => {
        const imageFile = e.target.files[0];
        const formData = new FormData();

        formData.append("image", imageFile);

        try {
            const result = await $.ajax({
                url: `/api/images/upload/street_art/1/user/1`,
                method: "post",
                data: formData,
                processData: false,
                contentType: false,
            });

            console.log(result)
            $("result").html(result)
        } catch (err) {
            console.log(err);
        }
    });
};