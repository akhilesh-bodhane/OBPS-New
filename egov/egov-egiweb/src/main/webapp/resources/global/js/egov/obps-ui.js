/*
 * OBPAS UI layer: small, dependency-free interaction helpers shared by every module.
 * Presentation only. It never changes form data, names, ids, handlers or navigation.
 *
 *   OBPS.toast(message, type, ms)   type: info | success | error | warning
 *   OBPS.initials(name)             "Sufina Kaur" -> "SK"
 *   OBPS.escapeHtml(text)
 */
(function (window, document) {
    'use strict';
    if (window.OBPS && window.OBPS.__ready) { return; }

    var OBPS = window.OBPS = window.OBPS || {};
    OBPS.__ready = true;

    /* ---------- theme (light / dark) ----------
     * Preference is stored in localStorage ('obps-theme' = light | dark); with no stored choice the OS setting is followed.
     * Applied immediately (this script is loaded in <head>) so there is no flash of the wrong theme.
     * Pages on the same host share the preference, so popups opened from the home page match it. */
    var THEME_KEY = 'obps-theme';
    var mql = window.matchMedia ? window.matchMedia('(prefers-color-scheme: dark)') : null;
    function storedTheme() { try { return window.localStorage.getItem(THEME_KEY); } catch (e) { return null; } }
    function effectiveTheme() {
        var s = storedTheme();
        if (s === 'dark' || s === 'light') { return s; }
        return mql && mql.matches ? 'dark' : 'light';
    }
    function syncToggles(t) {
        Array.prototype.forEach.call(document.querySelectorAll('[data-ob-theme-toggle]'), function (b) {
            var dark = t === 'dark';
            b.setAttribute('aria-pressed', String(dark));
            b.setAttribute('aria-label', dark ? 'Switch to light mode' : 'Switch to dark mode');
            b.title = dark ? 'Switch to light mode' : 'Switch to dark mode';
            var i = b.querySelector('.fa');
            if (i) { i.className = 'fa ' + (dark ? 'fa-sun-o' : 'fa-moon-o'); }
        });
    }
    function applyTheme(t) {
        document.documentElement.setAttribute('data-theme', t);
        syncToggles(t);
    }
    OBPS.setTheme = function (t) {
        if (t !== 'dark' && t !== 'light') { return; }
        try { window.localStorage.setItem(THEME_KEY, t); } catch (e) { /* private mode: still applies for this page */ }
        applyTheme(t);
    };
    OBPS.toggleTheme = function () { OBPS.setTheme(effectiveTheme() === 'dark' ? 'light' : 'dark'); };
    applyTheme(effectiveTheme());
    if (mql && mql.addEventListener) { mql.addEventListener('change', function () { if (!storedTheme()) { applyTheme(effectiveTheme()); } }); }
    window.addEventListener('storage', function (e) { if (e.key === THEME_KEY) { applyTheme(effectiveTheme()); } });
    var printed;
    window.addEventListener('beforeprint', function () { printed = document.documentElement.getAttribute('data-theme'); document.documentElement.setAttribute('data-theme', 'light'); });
    window.addEventListener('afterprint', function () { if (printed) { document.documentElement.setAttribute('data-theme', printed); } });

    OBPS.escapeHtml = function (s) {
        return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
            return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[c];
        });
    };

    OBPS.initials = function (name) {
        var parts = String(name || '').replace(/[^A-Za-z0-9\s.]/g, ' ').trim().split(/[\s.]+/).filter(Boolean);
        if (!parts.length) { return '?'; }
        if (parts.length === 1) { return parts[0].substring(0, 2).toUpperCase(); }
        return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    };

    OBPS.toast = function (message, type, ms) {
        var host = document.querySelector('.ob-toasts');
        if (!host) {
            host = document.createElement('div');
            host.className = 'ob-toasts';
            host.setAttribute('role', 'status');
            host.setAttribute('aria-live', 'polite');
            document.body.appendChild(host);
        }
        var t = document.createElement('div');
        t.className = 'ob-toast ' + (type || 'info');
        var msg = document.createElement('span');
        msg.textContent = message;
        var x = document.createElement('button');
        x.type = 'button';
        x.className = 'ob-toast-x';
        x.setAttribute('aria-label', 'Dismiss');
        x.innerHTML = '&times;';
        t.appendChild(msg);
        t.appendChild(x);
        host.appendChild(t);
        function close() {
            if (!t.parentNode) { return; }
            t.classList.add('leaving');
            setTimeout(function () { if (t.parentNode) { t.parentNode.removeChild(t); } }, 200);
        }
        x.addEventListener('click', close);
        setTimeout(close, ms || 4500);
        return t;
    };

    function ready(fn) {
        if (document.readyState !== 'loading') { fn(); } else { document.addEventListener('DOMContentLoaded', fn); }
    }

    ready(function () {
        /* ---- theme toggle: put one in the header action area, or float it on pages without a header (login) ---- */
        if (!document.querySelector('[data-ob-theme-toggle]')) {
            var tb = document.createElement('button');
            tb.type = 'button';
            tb.setAttribute('data-ob-theme-toggle', '');
            tb.innerHTML = '<i class="fa fa-moon-o" aria-hidden="true"></i>';
            var host = document.querySelector('.ob-actions, .ob-home-actions, .ob-ctop-actions');
            if (host) {
                tb.className = 'ob-iconbtn ob-theme-toggle';
                var anchor = host.querySelector('.ob-userwrap');
                host.insertBefore(tb, anchor || null);
            } else {
                tb.className = 'ob-theme-fab ob-theme-toggle';
                document.body.appendChild(tb);
            }
        }
        Array.prototype.forEach.call(document.querySelectorAll('[data-ob-theme-toggle]'), function (b) {
            b.addEventListener('click', OBPS.toggleTheme);
        });
        syncToggles(effectiveTheme());

        /* ---- DataTables on phones: label each cell with its column header (CSS shows it when the header row is hidden) ---- */
        function labelTables() {
            Array.prototype.forEach.call(document.querySelectorAll('table.dataTable'), function (t) {
                var ths = t.querySelectorAll('thead tr:last-child th');
                if (!ths.length) { return; }
                var labels = Array.prototype.map.call(ths, function (th) { return (th.textContent || '').replace(/\s+/g, ' ').trim(); });
                Array.prototype.forEach.call(t.querySelectorAll('tbody tr'), function (tr) {
                    Array.prototype.forEach.call(tr.children, function (td, i) {
                        if (td.tagName === 'TD' && labels[i] && !td.hasAttribute('data-label')) { td.setAttribute('data-label', labels[i]); }
                    });
                });
            });
        }
        labelTables();
        if (window.jQuery) { window.jQuery(document).on('draw.dt init.dt', labelTables); }

        /* ---- back-to-top ---- */
        var top = document.createElement('button');
        top.type = 'button';
        top.className = 'ob-totop';
        top.setAttribute('aria-label', 'Back to top');
        top.innerHTML = '<i class="fa fa-angle-up" aria-hidden="true" style="font-size:20px"></i>';
        top.addEventListener('click', function () { window.scrollTo({top: 0, behavior: 'smooth'}); });
        document.body.appendChild(top);
        var ticking = false;
        window.addEventListener('scroll', function () {
            if (ticking) { return; }
            ticking = true;
            window.requestAnimationFrame(function () {
                top.classList.toggle('show', window.pageYOffset > 480);
                ticking = false;
            });
        }, {passive: true});

        /* ---- busy state on the submit button (visual only; the button is never disabled, so its value is still posted) ---- */
        document.addEventListener('submit', function (e) {
            if (e.defaultPrevented) { return; }       // blocked by client-side validation or custom handlers
            var form = e.target;
            if (!form || form.target === '_blank' || form.hasAttribute('data-ob-nobusy')) { return; }
            var btn = e.submitter || form.querySelector('button[type=submit], input[type=submit]');
            if (!btn || btn.tagName === 'INPUT') { return; }
            btn.classList.add('ob-busy');
            setTimeout(function () { btn.classList.remove('ob-busy'); }, 8000);   // downloads / popups never navigate away
        }, false);
        window.addEventListener('pageshow', function () {
            Array.prototype.forEach.call(document.querySelectorAll('.btn.ob-busy'), function (b) { b.classList.remove('ob-busy'); });
        });

        /* ---- password visibility toggle: opt-in via data-ob-toggle-pass on the input ---- */
        Array.prototype.forEach.call(document.querySelectorAll('input[data-ob-toggle-pass]'), function (input) {
            var holder = input.parentNode;
            var btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'ob-eye';
            btn.setAttribute('aria-label', 'Show password');
            btn.setAttribute('aria-pressed', 'false');
            btn.innerHTML = '<i class="fa fa-eye" aria-hidden="true"></i>';
            btn.addEventListener('click', function () {
                var show = input.type === 'password';
                input.type = show ? 'text' : 'password';
                btn.setAttribute('aria-pressed', String(show));
                btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
                btn.firstChild.className = 'fa ' + (show ? 'fa-eye-slash' : 'fa-eye');
                input.focus();
            });
            holder.appendChild(btn);
        });

        /* ---- header dropdown menus (click + keyboard, works without Bootstrap) ---- */
        Array.prototype.forEach.call(document.querySelectorAll('[data-ob-menu]'), function (trigger) {
            var menu = trigger.parentNode.querySelector('.ob-menu');
            if (!menu) { return; }
            function set(open) {
                menu.classList.toggle('show', open);
                trigger.setAttribute('aria-expanded', String(open));
            }
            trigger.addEventListener('click', function (e) { e.stopPropagation(); set(!menu.classList.contains('show')); });
            document.addEventListener('click', function (e) { if (!menu.contains(e.target)) { set(false); } });
            document.addEventListener('keydown', function (e) { if (e.key === 'Escape') { set(false); } });
        });
    });
})(window, document);
