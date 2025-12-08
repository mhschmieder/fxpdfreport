/*
 * MIT License
 *
 * Copyright (c) 2020, 2025, Mark Schmieder. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the fxpdfreport Library
 *
 * You should have received a copy of the MIT License along with the fxpdfreport
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxpdfreport
 */
package com.mhschmieder.fxpdfreport.cad;

import com.mhschmieder.fxcadgraphics.Region2D;
import com.mhschmieder.fxcadgraphics.Surface;
import com.mhschmieder.fxcadgui.layout.SurfacesInformationPane;
import com.mhschmieder.jcommons.text.TextUtilities;
import com.mhschmieder.jpdfreport.PdfFonts;
import com.mhschmieder.jpdfreport.PdfTools;
import com.mhschmieder.jphysics.DistanceUnit;
import com.mhschmieder.jphysics.UnitConversion;
import com.pdfjet.Align;
import com.pdfjet.Cell;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.Point;
import com.pdfjet.Table;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Static utility class to export CAD graphics and GUI elements to PDF using the
 * PDFJet library. This keeps core libraries from adding PDFJet dependencies.
 */
public final class FxPdfJetCadExporter {

    // NOTE: The constructor is disabled, as this is a static class.
    private FxPdfJetCadExporter() {}

    public static Point exportSurfacesToPdf(
            final SurfacesInformationPane surfacesInformationPane,
            final PDF document,
            final Page page,
            final Point initialPoint,
            final PdfFonts borderlessTableFonts )
            throws Exception {
        // Collect the information fields to render to a single-column table.
        final String[] information = surfacesInformationPane
                .getSurfaceInformation();

        // Write the Surfaces Information Table, left-aligned.
        return PdfTools.writeInformationTable(
                document,
                page,
                initialPoint,
                borderlessTableFonts,
                Align.LEFT,
                information );
    }

    public void exportRegionToPdf(
            final Region2D region,
            final PDF document,
            final Page page,
            final Point initialPoint,
            final PdfFonts fonts,
            final NumberFormat pNumberFormat,
            final DistanceUnit distanceUnit ) {
        // NOTE: Regions are currently stored in User Units vs. Meters etc.
        final String distanceUnitLabel = distanceUnit.abbreviation();

        // Potentially adjust the floating-point precision of distances.
        final int precision = DistanceUnit.MILLIMETERS.equals( distanceUnit )
                ? 0
                : 2;
        final NumberFormat distanceNumberFormat = ( NumberFormat ) pNumberFormat.clone();
        distanceNumberFormat.setMaximumFractionDigits( precision );

        // Convert to User Units, as the report should follow those preferences.
        final double xConverted = UnitConversion.convertDistance(
                region.getX(), DistanceUnit.METERS, distanceUnit );
        final double yConverted = UnitConversion.convertDistance(
                region.getY(), DistanceUnit.METERS, distanceUnit );
        final double widthConverted = UnitConversion.convertDistance(
                region.getWidth(), DistanceUnit.METERS, distanceUnit );
        final double heightConverted = UnitConversion.convertDistance(
                region.getHeight(), DistanceUnit.METERS, distanceUnit );

        // Declare the Region Boundary column headers, then get the table.
        final String[] boundarySpanNames = new String[] { "EXTENTS" };
        final String[] boundaryColumnNames = new String[] {
                "LOWER LEFT CORNER (X, Y)",
                "SIZE (WIDTH, HEIGHT)" };
        final int numberOfBoundaryColumns = boundaryColumnNames.length;
        final int[] boundarySpanLengths = new int[] { numberOfBoundaryColumns };

        // Get a table to use for the Region Boundary.
        // NOTE: This also sets the column headers and their styles.
        final List<List<Cell>> boundaryTableData = new ArrayList<>();
        final Table boundaryTable = PdfTools.createTable( boundaryTableData,
                fonts,
                boundarySpanNames,
                boundarySpanLengths,
                boundaryColumnNames,
                numberOfBoundaryColumns,
                false );

        // Write the Region Boundary Table.
        final String lowerLeftCorner = TextUtilities.getFormattedQuantityPair(
                xConverted,
                yConverted,
                distanceNumberFormat,
                distanceUnitLabel );
        final String size = TextUtilities.getFormattedQuantityPair(
                widthConverted,
                heightConverted,
                distanceNumberFormat,
                distanceUnitLabel );

        final List< Cell > boundaryRowData = new ArrayList<>();

        PdfTools.addTableCell( boundaryRowData, fonts, lowerLeftCorner );
        PdfTools.addTableCell( boundaryRowData, fonts, size );

        boundaryTableData.add( boundaryRowData );

        // Write the table to as many pages as are required to fit.
        Point point = new Point(
                PdfTools.PORTRAIT_LEFT_MARGIN,
                initialPoint.getY() + 20 );
        point = PdfTools.writeTable(
                document,
                page,
                point,
                fonts,
                boundaryTableData,
                boundaryTable,
                Table.DATA_HAS_2_HEADER_ROWS,
                true,
                false );

        // Declare the Surfaces column headers, then get the table.
        final String[] surfacesSpanNames = new String[] { "SURFACES" };
        final String[] surfacesColumnNames = new String[] {
                "ID",
                "SURFACE NAME",
                "STATUS",
                "MATERIAL NAME" };
        final int numberOfSurfacesColumns = surfacesColumnNames.length;
        final int[] surfacesSpanLengths = new int[] { numberOfSurfacesColumns };

        // Manually size the column widths, as PDFjet is leaving too much wasted
        // space in the numeric columns and thus occasionally clipping the
        // verbose right-most Surface Material Name column.
        final int[] surfacesColumnWidths = new int[] {
                20, // COLUMN_SURFACE_ID
                180, // COLUMN_SURFACE_NAME
                100, // COLUMN_SURFACE_STATUS
                240 }; // COLUMN_SURFACE_MATERIAL_NAME

        // Get a table to use for the Region Surfaces.
        // NOTE: This also sets the column headers and their styles.
        final List< List< Cell > > surfacesTableData = new ArrayList<>();
        final Table surfacesTable = PdfTools.createTable(
                surfacesTableData,
                fonts,
                surfacesSpanNames,
                surfacesSpanLengths,
                surfacesColumnNames,
                numberOfSurfacesColumns,
                surfacesColumnWidths,
                false );

        // Write the Region Surfaces Table.
        final List< Surface > numberedSurfaces = region.getSurfaces();
        for ( final Surface surfaceReference : numberedSurfaces ) {
            final List< Cell > surfacesRowData = new ArrayList<>();

            final String status = surfaceReference.isSurfaceBypassed()
                    ? "Bypassed"
                    : "Enabled";
            PdfTools.addTableCell(
                    surfacesRowData,
                    fonts,
                    Integer.toString( surfaceReference.getSurfaceNumber() ) );
            PdfTools.addTableCell(
                    surfacesRowData,
                    fonts,
                    surfaceReference.getLabel() );
            PdfTools.addTableCell( surfacesRowData, fonts, status );
            PdfTools.addTableCell(
                    surfacesRowData,
                    fonts,
                    surfaceReference.getSurfaceMaterial().abbreviation() );

            surfacesTableData.add( surfacesRowData );
        }

        // Write the table to as many pages as are required to fit.
        point.setPosition( PdfTools.PORTRAIT_LEFT_MARGIN, point.getY() + 20.0f );
        PdfTools.writeTable(
                document,
                page,
                point,
                fonts,
                surfacesTableData,
                surfacesTable,
                Table.DATA_HAS_2_HEADER_ROWS,
                true,
                false );
    }
}
