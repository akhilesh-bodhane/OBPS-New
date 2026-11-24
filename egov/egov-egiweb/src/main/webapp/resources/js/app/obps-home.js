/*
 * OBPAS official home: dashboard overview + responsive navigation.
 * Presentation layer only. All figures are derived from the worklist JSON that homepageofficial.js
 * already requests from the server (GET inbox); no extra request is made and nothing is invented.
 */
(function ($, window, document) {
    'use strict';
    if (!$) { return; }

    var esc = (window.OBPS && window.OBPS.escapeHtml) || function (s) { return String(s == null ? '' : s); };

    /* ---------- header: date, initials ---------- */
    try {
        $('#obToday').text(new Date().toLocaleDateString(undefined, {weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'}));
    } catch (e) { /* older engines: leave blank */ }
    $('[data-ob-name]').each(function () {
        var n = $(this).attr('data-ob-name');
        if (n && window.OBPS) { $(this).text(window.OBPS.initials(n)); }
    });

    /* ---------- responsive navigation drawer ---------- */
    var $body = $('body');
    function setNav(open) {
        $body.toggleClass('ob-nav-open', open);
        $('#obNavToggle').attr('aria-expanded', String(open));
    }
    $('#obNavToggle').on('click', function () { setNav(!$body.hasClass('ob-nav-open')); });
    $('#obScrim').on('click', function () { setNav(false); });
    $(document).on('keydown', function (e) { if (e.key === 'Escape') { setNav(false); } });
    $(window).on('resize', function () { if (window.innerWidth > 991) { setNav(false); } });
    // choosing a menu leaf or a search result opens its own window: close the drawer so the page is usable again
    $(document).on('click', '#menu li a, .search_list a', function () {
        if (window.innerWidth <= 991 && $(this).closest('li').find('.levelHolderClass').length === 0) { setNav(false); }
    });

    /* ---------- dashboard ---------- */
    var FEATURES = 'width=900, height=700, top=300, left=150,scrollbars=yes';

    function elapsedDays(r) {
        var n = parseFloat(String(r.elapsed == null ? '' : r.elapsed).replace(/[^0-9.\-]/g, ''));
        return isNaN(n) ? 0 : n;
    }

    function setKpi(key, value) {
        $('#obKpis [data-kpi="' + key + '"] .ob-stat-num').text(value);
    }

    function openItem(r) {
        if (!r || !r.link) { return; }
        var w = window.open(r.link, String(r.id) + '', FEATURES);
        if (w) {
            if (window.openedWindows) { window.openedWindows.push(w); }
            w.focus();
        }
    }

    var lastRows = [];

    function render(rows) {
        lastRows = rows;
        var total = rows.length;
        var overdue = rows.filter(function (r) { return r.withinSla === false; });
        var within = total - overdue.length;

        // group by type of work
        var groups = {}, order = [];
        rows.forEach(function (r) {
            var k = r.task || 'Other';
            if (!groups[k]) { groups[k] = {task: k, n: 0, late: 0}; order.push(k); }
            groups[k].n++;
            if (r.withinSla === false) { groups[k].late++; }
        });

        setKpi('total', total);
        setKpi('within', within);
        setKpi('overdue', overdue.length);
        setKpi('types', order.length);
        $('#obKpis [data-kpi="overdue"]').toggleClass('is-zero', overdue.length === 0);

        var sub = total === 0 ? 'Your worklist is clear. New items will appear here.'
            : 'You have ' + total + (total === 1 ? ' item' : ' items') + ' in your worklist' +
              (overdue.length ? ', ' + overdue.length + ' past SLA.' : ', all within SLA.');
        $('#obWelcomeSub').text(sub);

        var $grid = $('#obDashGrid');
        if (!total) { $grid.prop('hidden', true); return; }

        // bars
        var list = order.map(function (k) { return groups[k]; }).sort(function (a, b) { return b.n - a.n; });
        var max = list[0].n, shown = list.slice(0, 6), html = '';
        shown.forEach(function (g) {
            var wOk = ((g.n - g.late) / max) * 100, wBad = (g.late / max) * 100;
            html += '<li><button type="button" class="ob-bar" data-task="' + esc(g.task) + '" title="Show ' + esc(g.task) + ' in the worklist">' +
                '<span class="ob-bar-top"><span class="ob-bar-name">' + esc(g.task) + '</span><b>' + g.n + '</b></span>' +
                '<span class="ob-bar-track"><i class="ok" style="width:' + wOk.toFixed(1) + '%"></i><i class="bad" style="width:' + wBad.toFixed(1) + '%"></i></span>' +
                '</button></li>';
        });
        $('#obByType').html(html);
        $('#obByTypeNote').text(list.length > shown.length ? 'Top ' + shown.length + ' of ' + list.length : list.length + (list.length === 1 ? ' type' : ' types'));

        // attention list: most overdue first; otherwise the latest items
        var attn, title;
        if (overdue.length) {
            attn = overdue.slice().sort(function (a, b) { return elapsedDays(b) - elapsedDays(a); }).slice(0, 5);
            title = overdue.length + ' overdue';
            $('#obAttnNote').attr('class', 'ob-chip bad');
        } else {
            attn = rows.slice(0, 5);
            title = 'All within SLA';
            $('#obAttnNote').attr('class', 'ob-chip ok');
        }
        $('#obAttnNote').text(title);
        var ah = '';
        attn.forEach(function (r, i) {
            var late = r.withinSla === false;
            ah += '<li><button type="button" class="ob-attn-item' + (late ? ' late' : '') + '" data-i="' + i + '">' +
                '<span class="ob-attn-main"><span class="ob-attn-task">' + esc(r.task) + '</span>' +
                '<span class="ob-attn-meta">' + esc(r.sender) + (r.status ? ' &middot; ' + esc(r.status) : '') + '</span></span>' +
                '<span class="ob-attn-age">' + (r.elapsed != null && r.elapsed !== '' ? esc(r.elapsed) + (isNaN(+r.elapsed) ? '' : ' d') : '') + '</span>' +
                '<i class="fa fa-angle-right" aria-hidden="true"></i></button></li>';
        });
        $('#obAttn').data('rows', attn).html(ah);
        $grid.prop('hidden', false);
    }

    $('#obAttn').on('click', '.ob-attn-item', function () {
        var rows = $('#obAttn').data('rows') || [];
        openItem(rows[$(this).data('i')]);
    });

    $('#obByType').on('click', '.ob-bar', function () {
        var task = $(this).attr('data-task');
        var $pill = $('#natureofwork a[data-task="' + escape(task) + '"]');
        if (!$('#worklist').is(':visible')) {            // on Drafts / Notifications: go back to the worklist first
            $('.workspace[data-work="worklist"]').trigger('click');
            return;
        }
        if ($pill.length) { $pill.trigger('click'); }
        var tbl = document.getElementById('official_inbox');
        if (tbl && tbl.scrollIntoView) { tbl.scrollIntoView({behavior: 'smooth', block: 'start'}); }
    });

    // DataTables raises xhr.dt after every worklist load (initial and after workspace switches)
    $('#official_inbox').on('xhr.dt', function (e, settings, json) {
        if ($.isArray(json)) { render(json); }
    });
})(window.jQuery, window, document);
