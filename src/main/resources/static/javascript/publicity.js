function getDeploymentContextPath(url) {
    if (url == null)
        url = new URL(window.location.href);
    const deployPath = url.pathname.split('/')[1];
    if (deployPath === 'test' || deployPath === 'prod')
        return '/' + deployPath;
    else
        return '';
}

function publicityHandler(checkbox){
    const url = new URL(window.location.href);
    const gardenID = url.searchParams.get('gardenID');
    let params = new URLSearchParams();
    params.append('_csrf', getCsrfToken());
    params.append("isPublic", checkbox.checked)
    params.append("gardenID", gardenID)
    fetch(getDeploymentContextPath(url) + '/view-garden', {
        method: 'PATCH',
        body: params
    }).then(response => {
        switchBadge(checkbox.checked)
    })
}

function switchBadge(isPublic) {
    const publicBadge = document.getElementById("publicBadge")
    const privateBadge = document.getElementById("privateBadge")
    const makeGardenPublicMessage = document.getElementById("makeGardenPublicMessage")

    publicBadge.hidden = !isPublic;
    privateBadge.hidden = isPublic;
    makeGardenPublicMessage.hidden = isPublic;
}

function getCsrfToken() {
    let cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, 'XSRF-TOKEN'.length + 1) === ('XSRF-TOKEN' + '=')) {
                cookieValue = decodeURIComponent(cookie.substring('XSRF-TOKEN'.length + 1));
                break;
            }
        }
    }
    console.log('XSRF-TOKEN: ' + cookieValue)
    return cookieValue;
}