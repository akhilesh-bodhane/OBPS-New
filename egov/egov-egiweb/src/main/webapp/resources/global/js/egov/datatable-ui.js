/**
 * Shared behaviour for every jQuery DataTable:
 *  - the table scrolls inside its own container (vertical and horizontal), with a sticky header,
 *    so the page itself does not grow with 100 rows / "All";
 *  - when "All" is the page size there is no concept of pages: the pager is removed and the page position is reset;
 *  - a visible notice when "All" had to be limited by the server (use Export to Excel for the full set);
 *  - a failed table request always clears the loading overlay.
 * Opt a table out with the attribute data-no-dt-ui. Tables that configure DataTables scrollX/scrollY keep their own scroller.
 */
(function($) {
	'use strict';

	var ALL = -1;

	function isAll(settings) {
		return settings._iDisplayLength === ALL;
	}

	function wrapForScroll(node) {
		if (!$(node).parent().hasClass('dt-scroll')) {
			$(node).wrap('<div class="dt-scroll"></div>');
		}
		// a header with no colour of its own would let rows show through the sticky header
		$(node).find('thead th').each(function() {
			var bg = window.getComputedStyle(this).backgroundColor;
			if (!bg || bg === 'transparent' || bg === 'rgba(0, 0, 0, 0)') {
				this.style.backgroundColor = '#f5f5f5';
			}
		});
	}

	function updatePager(settings) {
		var $wrapper = $(settings.nTableWrapper);
		var $notice = $wrapper.find('.dt-all-notice').remove();
		if (!isAll(settings)) {
			return;
		}
		// no pages when everything is shown: remove the controls rather than hide them
		$wrapper.find('.dataTables_paginate').empty();
		var shown = settings.aiDisplay ? settings.aiDisplay.length : 0;
		var total = settings.fnRecordsDisplay();
		if (total > shown && shown > 0) {
			$('<div class="dt-all-notice text-warning"></div>')
					.text('Showing the first ' + shown + ' of ' + total + ' records. Use Export All to Excel to get every record.')
					.insertAfter($wrapper.find('.dataTables_info').first());
		}
	}

	$(document).on('init.dt', function(e, settings) {
		var node = settings.nTable;
		if ($(node).is('[data-no-dt-ui]') || node.__dtUi) {
			return;
		}
		node.__dtUi = true;
		// DataTables' own scrollX/scrollY already gives a fixed header and its own scroller: do not nest another
		var ownScroll = settings.oScroll && (settings.oScroll.sY || settings.oScroll.sX);
		if (!ownScroll) {
			wrapForScroll(node);
		}

		// any page-size change starts from the first row; "All" has no page to preserve
		var wrapper = settings.nTableWrapper;
		if (wrapper && wrapper.addEventListener) {
			wrapper.addEventListener('change', function(ev) {
				if ($(ev.target).closest('.dataTables_length').length) {
					settings._iDisplayStart = 0;
				}
			}, true);
		}

		$(node).on('draw.dt', function() {
			updatePager(settings);
		});
		updatePager(settings);
	});

	// a failed or non-JSON response must never leave the loading overlay up
	$(document).on('error.dt xhr.dt', function(e) {
		if (e.type === 'error') {
			$('.loader-class').modal('hide');
		}
	});
})(jQuery);
