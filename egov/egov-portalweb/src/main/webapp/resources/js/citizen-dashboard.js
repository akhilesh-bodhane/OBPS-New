/*
 * Citizen dashboard presentation helpers (portal home).
 * Reads only values already rendered by the server / citizen.js; it never issues requests of its own.
 */
(function ($) {
    'use strict';
    if (!$) { return; }

    function num(sel) {
        var n = parseInt(($(sel).text() || '').replace(/[^0-9]/g, ''), 10);
        return isNaN(n) ? 0 : n;
    }

    /* ---- avatar initials from the logged-in user's name ---- */
    $('[data-ob-initials]').each(function () {
        var name = $(this).attr('data-ob-initials');
        if (name && window.OBPS) { $(this).text(window.OBPS.initials(name)); }
    });

    /* ---- status distribution, derived from the three counters on the page ---- */
    function renderDistribution() {
        var total = num('#totalServicesAppliedSize'),
            pending = num('#totalServicesPendingSize'),
            done = num('#totalServicesCompletedSize'),
            other = Math.max(total - pending - done, 0),
            sum = pending + done + other;
        var $card = $('#obDist');
        if (!sum) { $card.prop('hidden', true); return; }
        var parts = [
            {label: 'Under scrutiny', n: pending, cls: 'p'},
            {label: 'Completed', n: done, cls: 'd'},
            {label: 'Other statuses', n: other, cls: 'o'}
        ].filter(function (p) { return p.n > 0; });
        var bar = '', legend = '';
        parts.forEach(function (p) {
            var pct = (p.n / sum) * 100;
            bar += '<span class="seg ' + p.cls + '" style="width:' + pct.toFixed(2) + '%" title="' + p.label + ': ' + p.n + '"></span>';
            legend += '<li><span class="dot ' + p.cls + '"></span>' + p.label + '<b>' + p.n + '</b><em>' + Math.round(pct) + '%</em></li>';
        });
        $('#obDistBar').html(bar);
        $('#obDistLegend').html(legend);
        $('#obDistTotal').text(sum + ' total');
        $card.prop('hidden', false);
    }

    $(function () {
        renderDistribution();
        // citizen.js rewrites the counters after a service-group change; follow them.
        if (window.MutationObserver) {
            var mo = new MutationObserver(function () { renderDistribution(); });
            ['#totalServicesAppliedSize', '#totalServicesPendingSize', '#totalServicesCompletedSize'].forEach(function (s) {
                var el = document.querySelector(s);
                if (el) { mo.observe(el, {childList: true, characterData: true, subtree: true}); }
            });
        }
        // make the stat cards keyboard-operable
        $('.bpa-service-card').on('keydown', function (e) {
            if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); $(this).find('.bpa-home-card').trigger('click'); /* bubbles to the card handler once */ }
        });
    });
})(window.jQuery);
