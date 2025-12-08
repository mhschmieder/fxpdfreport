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

import com.mhschmieder.fxgui.layout.LogoPane;
import com.mhschmieder.jpdfreport.PdfTools;
import com.pdfjet.Image;
import com.pdfjet.PDF;
import com.pdfjet.Page;

import java.awt.image.BufferedImage;

public final class FxPdfJetExporter {

    // NOTE: The constructor is disabled, as this is a static class.
    private FxPdfJetExporter() {}

    public static void exportLogoToPdf( final LogoPane logoPane,
                                        final PDF document,
                                        final Page visualizationPage,
                                        final double xOffset,
                                        final double yOffset,
                                        final double scaleFactor )
            throws Exception {
        // Convert the AWT BufferedImage logo snapshot to a PdfJet Image.
        final BufferedImage logo = logoPane.getLogoForReport();
        final Image logoImage = PdfTools.getImageSnapshot( document, logo );
        if ( logoImage != null ) {
            logoImage.setPosition( xOffset, yOffset );
            logoImage.scaleBy( scaleFactor );
            logoImage.drawOn( visualizationPage );
        }
    }
}
