package org.mt4jx.components.visibleComponents.widgets.pdf;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import processing.core.PImage;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


public class PDFRenderer {
	
	public static Image loadImage(File pdf, double scaleFactor, int pageNumber) throws IOException {
        PDFFile pdffile = null;
		File file = pdf;
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		pdffile = new PDFFile(buf);

        PDFPage page = pdffile.getPage(pageNumber);
		int w = (int)(page.getBBox().getWidth());
		int h = (int)(page.getBBox().getHeight());
        Rectangle clip = new Rectangle(0,0,w,h);
        Image img = page.getImage(
        		(int)(w*scaleFactor),(int)(h*scaleFactor), //width & height
                clip, // clip rect
                null, // null for the ImageObserver
                true, // fill background with white
                true  // block until drawing is done
                );
        return img;
	}
	
	public static Image loadImage(File pdf, int width, int height, int pageNumber) throws IOException {
        PDFFile pdffile = null;
		File file = pdf;
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		pdffile = new PDFFile(buf);

        PDFPage page = pdffile.getPage(pageNumber);

        Rectangle clip = new Rectangle(0,0,(int)page.getBBox().getWidth(),(int)page.getBBox().getHeight());
        Image img = page.getImage(
        		width, height, //width & height
                clip, // clip rect
                null, // null for the ImageObserver
                true, // fill background with white
                true  // block until drawing is done
                );
        return img;
	}
	
	
	public static Image loadImage(File pdf) throws IOException {
		return loadImage(pdf, 1, 0);
	}
	public static PImage loadPImage(File pdf) throws IOException {
		return new PImage(loadImage(pdf));
	}
	public static PImage loadPImage(File pdf, float scaleFactor) throws IOException {
		return new PImage(loadImage(pdf, scaleFactor, 0));
	}
	public static PImage loadPImage(File pdf, int width, int height) throws IOException {
		return new PImage(loadImage(pdf, width, height, 0));
	}
}
