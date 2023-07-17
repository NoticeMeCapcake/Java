function sendHttpRequest(url, method, formData) {
    let requestOptions = {
        method: method,
        body: formData
    };

    return fetch(url, requestOptions)
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Ошибка: ' + response.status);
            }
            return response.json();
        });
}

function updateProgressBar(progress, progressBarId) {
    let progressBar = document.getElementById(progressBarId);
    // let currentProgress = parseInt(progressBar.style.width) || 0;
    let newProgress = progress;
    progressBar.style.width = newProgress + '%';
    progressBar.setAttribute('aria-valuenow', newProgress);
    progressBar.innerText = newProgress + '%';
}