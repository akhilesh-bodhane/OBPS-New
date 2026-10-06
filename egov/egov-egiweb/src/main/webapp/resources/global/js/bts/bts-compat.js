/*
 * Bootstrap 3 -> Bootstrap 5 compatibility layer.
 * Load AFTER jquery and bts5.js (Bootstrap 5.3 bundle). It lets the existing BS3 markup and the
 * existing jQuery calls ($.fn.modal / tooltip / popover / tab / collapse / dropdown, 'in' class,
 * data-toggle attributes, li.active tabs) keep working unchanged.
 */
(function (window, document, $) {
    'use strict';
    if (!$ || !window.bootstrap) { return; }

    // ---- 1. data-* attributes: data-toggle="modal" -> data-bs-toggle="modal" ----------------------------------
    var ATTRS = ['toggle', 'target', 'dismiss', 'parent', 'backdrop', 'keyboard', 'placement', 'trigger', 'content',
        'html', 'animation', 'container', 'delay', 'slide', 'slide-to', 'ride', 'spy', 'offset', 'selector',
        'template', 'title', 'interval', 'pause', 'wrap', 'boundary', 'focus', 'dismiss'];
    var TRIGGERS = '[data-toggle],[data-dismiss],[data-spy],[data-slide],[data-slide-to],[data-ride]';

    function mirrorAttrs(el) {
        for (var i = 0; i < ATTRS.length; i++) {
            var name = ATTRS[i];
            if (el.hasAttribute('data-' + name) && !el.hasAttribute('data-bs-' + name)) {
                var value = el.getAttribute('data-' + name);
                if (name === 'toggle' && value === 'pill') { value = 'tab'; }
                el.setAttribute('data-bs-' + name, value);
            }
        }
        // tab anchors: BS5 reads/sets .active on the anchor, BS3 markup keeps it on the <li>
        var toggle = el.getAttribute('data-toggle');
        if ((toggle === 'tab' || toggle === 'pill') && el.parentNode && $(el.parentNode).is('li.active')) {
            el.classList.add('active');
        }
    }

    function fixTree(root) {
        if (root.nodeType !== 1) { return; }
        if (root.matches(TRIGGERS)) { mirrorAttrs(root); }
        var found = root.querySelectorAll(TRIGGERS);
        for (var i = 0; i < found.length; i++) { mirrorAttrs(found[i]); }
        if (root.classList.contains('dropdown-menu-right')) { root.classList.add('dropdown-menu-end'); }
        var menus = root.querySelectorAll('.dropdown-menu-right');
        for (var j = 0; j < menus.length; j++) { menus[j].classList.add('dropdown-menu-end'); }
        syncInShow(root, true);
    }

    // ---- 2. BS3 .in <-> BS5 .show ---------------------------------------------------------------------------
    var STATEFUL = '.collapse,.fade,.tab-pane,.modal,.popover,.tooltip';

    // last-seen {in, show} per element; comparing against it (not the mutation's oldValue) keeps this correct when
    // several class changes are delivered in one batch
    var seen = typeof WeakMap === 'function' ? new WeakMap() : null;

    function syncOne(t) {
        var cl = t.classList;
        var cur = { i: cl.contains('in'), s: cl.contains('show') };
        var prev = seen && seen.get(t);
        if (!prev) {
            if (cur.i && !cur.s) { cl.add('show'); cur.s = true; }
        } else if (cur.i !== prev.i) {
            if (cur.s !== cur.i) { cl.toggle('show', cur.i); cur.s = cur.i; }
        } else if (cur.s !== prev.s) {
            if (cur.i !== cur.s) { cl.toggle('in', cur.s); cur.i = cur.s; }
        }
        if (seen) { seen.set(t, cur); }
    }

    function syncInShow(el, deep) {
        if (el.matches && el.matches(STATEFUL)) { syncOne(el); }
        if (deep && el.querySelectorAll) {
            var inner = el.querySelectorAll(STATEFUL);
            for (var i = 0; i < inner.length; i++) { syncOne(inner[i]); }
        }
    }

    function start() {
        fixTree(document.documentElement);
        new MutationObserver(function (records) {
            records.forEach(function (r) {
                if (r.type === 'childList') {
                    for (var i = 0; i < r.addedNodes.length; i++) { fixTree(r.addedNodes[i]); }
                } else if (r.type === 'attributes' && r.attributeName === 'class') {
                    syncInShow(r.target, false);
                } else if (r.type === 'attributes') {
                    mirrorAttrs(r.target);
                }
            });
        }).observe(document.documentElement, {
            childList: true, subtree: true, attributes: true,
            attributeFilter: ['class', 'data-toggle', 'data-target', 'data-dismiss']
        });
    }
    if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', start); } else { start(); }

    // ---- 2b. BS3 merged the trigger's data-* options into the target; BS5 only reads them from the target ------
    var COPY = ['parent', 'backdrop', 'keyboard'];
    // window capture runs before BS5's delegated (capture) handlers on document
    window.addEventListener('click', function (e) {
        var trigger = e.target.closest ? e.target.closest('[data-toggle="collapse"],[data-toggle="modal"]') : null;
        if (!trigger) { return; }
        var sel = trigger.getAttribute('data-target') || trigger.getAttribute('href');
        if (!sel || sel === '#' || sel.charAt(0) === '#' && sel.length < 2) { return; }
        var targets;
        try { targets = document.querySelectorAll(sel); } catch (err) { return; }
        for (var i = 0; i < targets.length; i++) {
            for (var j = 0; j < COPY.length; j++) {
                var v = trigger.getAttribute('data-' + COPY[j]);
                if (v !== null && !targets[i].hasAttribute('data-bs-' + COPY[j])) { targets[i].setAttribute('data-bs-' + COPY[j], v); }
            }
        }
    }, true);

    // ---- 3. Tabs / dropdowns: keep BS3 state classes on <li> ------------------------------------------------
    $(document).on('shown.bs.tab', function (e) {
        var li = $(e.target).closest('li');
        if (li.length) { li.addClass('active').siblings('li').removeClass('active'); }
        if (e.relatedTarget) { $(e.relatedTarget).closest('li').not(li).removeClass('active'); }
    });
    $(document).on('show.bs.dropdown', function (e) { $(e.target).parent().addClass('open'); });
    $(document).on('hide.bs.dropdown', function (e) { $(e.target).parent().removeClass('open'); });

    // ---- 4. jQuery plugin API ------------------------------------------------------------------------------
    // BS3 defined $.fn.modal etc. as soon as bts.js loaded; BS5 only does it on DOMContentLoaded, which is after
    // jQuery's own ready callbacks. Define them right away, and again on DOMContentLoaded because BS5 then
    // re-registers its plain versions over ours.
    function normalizePlacement(config) {
        if (config && typeof config === 'object' && typeof config.placement === 'string') {
            return $.extend({}, config, { placement: config.placement.replace(/^autos+w+$/, 'auto') });
        }
        return config;
    }

    function definePlugins() {
        Object.keys(window.bootstrap).forEach(function (cls) {
            var Class = window.bootstrap[cls];
            if (!Class || typeof Class.jQueryInterface !== 'function' || !Class.NAME) { return; }
            var name = Class.NAME;
            var current = $.fn[name];
            if (current && current.__bsCompat) { return; }
            var plugin = function (config) { return Class.jQueryInterface.call(this, config); };
            if (name === 'tooltip' || name === 'popover') {
                plugin = function (config) {
                    if (config === 'destroy') { config = 'dispose'; }
                    else if (config === 'fixTitle') { return this; }
                    return Class.jQueryInterface.call(this, normalizePlacement(config));
                };
            }
            plugin.Constructor = Class;
            plugin.noConflict = function () { return plugin; };
            plugin.__bsCompat = true;
            $.fn[name] = plugin;
        });
    }
    definePlugins();
    if (document.readyState === 'loading') { document.addEventListener('DOMContentLoaded', definePlugins); }

    // $el.data('bs.modal') etc. -> instance
    var oldData = $.fn.data;
    $.fn.data = function (key) {
        if (arguments.length === 1 && typeof key === 'string' && key.indexOf('bs.') === 0) {
            var name = key.slice(3);
            var cls = window.bootstrap[name.charAt(0).toUpperCase() + name.slice(1)];
            return cls && this[0] && cls.getInstance ? cls.getInstance(this[0]) : undefined;
        }
        return oldData.apply(this, arguments);
    };
    if (window.bootstrap.Tooltip && !window.bootstrap.Tooltip.prototype.tip) {
        window.bootstrap.Tooltip.prototype.tip = function () { return $(this._getTipElement()); };
    }
})(window, document, window.jQuery);
