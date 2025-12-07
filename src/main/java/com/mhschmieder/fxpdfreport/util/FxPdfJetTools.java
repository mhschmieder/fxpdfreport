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
package com.mhschmieder.fxpdfreport.util;

import com.mhschmieder.fxgraphics.image.ImageUtilities;
import com.mhschmieder.jpdfreport.PdfFonts;
import com.mhschmieder.jpdfreport.PdfTools;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import javafx.scene.layout.Region;

import java.awt.image.BufferedImage;

/**
 * Static utility class to wrap the export of JavaFX nodes on the screen to PDF
 * graphics in report format using PDFJet alongside specific layout assumptions
 * in the context of visualization panes with charts and annotation.
 */
public final class FxPdfJetTools {

    // NOTE: The constructor is disabled, as this is a static class.
    private FxPdfJetTools() {}

    public static double writeVisualization( final PDF document,
                                             final Page visualizationPage,
                                             final PdfFonts fonts,
                                             final String chartLabel,
                                             final double layoutWidth,
                                             final boolean useChart1,
                                             final Region chart1,
                                             final boolean useChart2,
                                             final Region chart2,
                                             final boolean useLegend,
                                             final Region chartLegend )
            throws Exception {
        // Get an AWT BufferedImage as a snapshot of the first chart.
        final BufferedImage chart1Snapshot = ImageUtilities
                .getBufferedImageSnapshot( chart1 );

        // Get an AWT BufferedImage as a snapshot of the second chart.
        final BufferedImage chart2Snapshot = ImageUtilities
                .getBufferedImageSnapshot( chart2 );

        // Get an AWT BufferedImage as a snapshot of the chart legend.
        final BufferedImage chartLegendSnapshot = ImageUtilities
                .getBufferedImageSnapshot( chartLegend );

        // Write the visualization and return the metadata y-axis adjustment.
        return PdfTools.writeVisualization(
                document,
                visualizationPage,
                fonts,
                chartLabel,
                layoutWidth,
                useChart1,
                chart1Snapshot,
                useChart2,
                chart2Snapshot,
                useLegend,
                chartLegendSnapshot );
    }
}
