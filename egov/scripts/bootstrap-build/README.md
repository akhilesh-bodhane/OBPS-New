# Bootstrap 5 build

`npm install && npm run build` compiles `src/bts5.scss` into `egov-egiweb/.../resources/global/css/bts/bts5.css`
(Bootstrap 5.3.8 tuned to the BS3 look/breakpoints + the verbatim BS3 rules in `src/_bs3-legacy.scss`).
Copy the result to the `egov-edcrweb` copies of `global/css/bts/` as well.
`_bs3-legacy.scss` is generated: `node extract-legacy.js <old Bootstrap 3.3 bts.css>`.
JS side: `bts5.js` (Bootstrap 5.3.8 bundle) + `bts-compat.js` (BS3 markup/jQuery API shim) next to it in `resources/global/js/bts/`.
