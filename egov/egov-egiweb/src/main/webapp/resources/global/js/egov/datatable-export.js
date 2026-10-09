/**
 * Adds an "Export All to Excel" button to every server-side (POST) DataTable.
 * The button re-posts the table's last request (filters, sort) with the list of visible columns; the server replays the
 * search page by page (DataTableExportFilter) and returns an .xlsx holding every matching record, not just the page on
 * screen. Nothing needs to be configured per screen. Opt a table out with data-no-excel-export="true".
 */
(function($) {
	'use strict';

	function fileNameFor(tableNode) {
		var base = tableNode.id || document.title || 'export';
		return base.replace(/_?table$/i, '').replace(/[^\w-]+/g, '_') || 'export';
	}

	function exportAll(dt, url, fileName, $btn) {
		var params = dt.ajax.params();
		if (!params) {
			return;
		}
		var columns = [];
		dt.settings()[0].aoColumns.forEach(function(col, idx) {
			if (typeof col.mData === 'string' && col.bVisible) {
				columns.push({ header: $(dt.column(idx).header()).text(), key: col.mData });
			}
		});
		var body = new URLSearchParams();
		Object.keys(params).forEach(function(k) {
			if (params[k] !== null && params[k] !== undefined) {
				body.append(k, params[k]);
			}
		});
		body.append('exportColumns', JSON.stringify(columns));
		body.append('exportFileName', fileName);
		$btn.prop('disabled', true);
		fetch(url, {
			method : 'POST',
			credentials : 'same-origin',
			headers : { 'Content-Type' : 'application/x-www-form-urlencoded' },
			body : body.toString()
		}).then(function(resp) {
			if (!resp.ok) {
				// the server answers with a plain-text reason (no records, busy, search failed ...)
				return resp.text().then(function(text) {
					var plain = /^\s*</.test(text) ? '' : text.trim();
					throw new Error(plain || 'Unable to export (HTTP ' + resp.status + '). Please try again.');
				});
			}
			return resp.blob();
		}).then(function(blob) {
			var a = document.createElement('a');
			a.href = URL.createObjectURL(blob);
			a.download = fileName + '.xlsx';
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
			URL.revokeObjectURL(a.href);
		}).catch(function(e) {
			if (window.bootbox) {
				bootbox.alert(e.message);
			} else {
				alert(e.message);
			}
		}).finally(function() {
			$btn.prop('disabled', false);
		});
	}

	$(document).on('init.dt', function(e, settings) {
		var node = settings.nTable;
		if (!settings.oFeatures.bServerSide || $(node).data('no-excel-export')) {
			return;
		}
		var ajax = settings.ajax;
		var url = typeof ajax === 'string' ? ajax : (ajax && ajax.url);
		var type = ajax && ajax.type ? String(ajax.type).toUpperCase() : 'GET';
		if (!url || type !== 'POST') {
			return;
		}
		var dt = new $.fn.dataTable.Api(settings);
		var btnId = 'excelExportAll_' + (node.id || $('table').index(node));
		$('#' + btnId).closest('div').remove();
		var $btn = $('<button type="button" class="btn btn-default" style="margin:0 0 5px 5px"></button>')
				.attr('id', btnId).text('Export All to Excel');
		$(settings.nTableWrapper).before($('<div class="text-right"></div>').append($btn));
		$btn.on('click', function() {
			exportAll(dt, url, fileNameFor(node), $btn);
		});
	});
})(jQuery);
