document.addEventListener("DOMContentLoaded", () => {
    const password = document.querySelector("[data-password]");
    const confirm = document.querySelector("[data-confirm]");
    const mismatch = document.querySelector("[data-mismatch]");
    if (!password || !confirm || !mismatch) {
        return;
    }
    const check = () => {
        const show = confirm.value.length > 0 && password.value !== confirm.value;
        mismatch.hidden = !show;
    };
    password.addEventListener("input", check);
    confirm.addEventListener("input", check);
});
