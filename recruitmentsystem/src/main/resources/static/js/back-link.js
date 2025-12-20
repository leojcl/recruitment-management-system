document.addEventListener("DOMContentLoaded", function () {
    const backlink = document.getElementById("back-link");
    if (!backlink) return;

    const ref = document.referrer;

    backlink.addEventListener("click", function (e) {
        e.preventDefault();

        if (ref && ref !== window.location.href) {
            history.back();
        } else {
            window.location.href = "/";
        }
    });
});
