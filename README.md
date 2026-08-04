# fxpdfreport

JavaFX wrappers for using jpdreport and its dependency implementation references
in PDFJet to produce custom formatted reports as PDF documents.

NOTE: As jpdfreport itself is in flux and may shift to a different underlying
third-party PDF writer soon, use this library with caution, but the plan is to
make any lower level transition transparent to this library.

This is not an exporter per se, which is why that isn't the library's name. It
simply provides tools for commonalizing repetitive tasks when preparing tables
and other cumbersome entities for PDF export using the other toolkits mentioned
above, when doing so in the context of a JavaFX application.
