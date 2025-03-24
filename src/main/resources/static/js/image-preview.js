// Función para vincular un input de archivo a uno de imagen
// previewImgId es el input donde se visualizara la foto subida a fileInputId
function imagePreview(previewImage, fileInput) {

    previewImage.addEventListener('click', function (event) {
        event.preventDefault();
        fileInput.click();
    });

    fileInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        const reader = new FileReader();

        reader.onload = function () {
            previewImage.src = reader.result;
        };

        if (file) {
            reader.readAsDataURL(file);
        }
    });
}