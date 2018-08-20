var loadCommon = (function() {
    var executed = false;
    return function() {
        if (!executed) {
            executed = true;

        }
    };
})();

loadCommon
