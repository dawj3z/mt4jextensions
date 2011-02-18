/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4jx.components.visibleComponents.font;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.logging.ILogger;
import org.mt4j.util.logging.Log4jLogger;
import org.mt4j.util.logging.MTLoggerFactory;
import org.mt4jx.components.visibleComponents.font.fontFactories.EnhancedBitmapFontFactory;
import org.mt4jx.components.visibleComponents.font.fontFactories.EnhancedSvgFontFactory;
import org.mt4jx.components.visibleComponents.font.fontFactories.EnhancedTTFontFactory;
import org.mt4jx.components.visibleComponents.font.fontFactories.IEnhancedFontFactory;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * <p>
 * Enhanced manager to obtaining and caching fonts.  Based upon 
 * <tt>org.mt4j.components.visibleComponents.font.FontManager</tt>
 * 
 * @author Christopher Ruff -- the FontManager class from which this
 *   class is derived.
 * @author R.Scarberry -- new methods and changes.
 */
public class EnhancedFontManager {
  
	private static final ILogger logger = MTLoggerFactory.getLogger(EnhancedFontManager.class.getName());;
	static{
      logger.setLevel(ILogger.INFO);
	}
	
	/** The font manager. */
	private static EnhancedFontManager fontManager;
	
	/** Maps font names to lists containing fonts with those names. */
	private Map<String, List<IFont>> fonts;
	
	/** The suffix to factory. */
	private HashMap<String, IEnhancedFontFactory> suffixToFactory;
	
	// Paths to directories containing fonts, separated by File.pathSeparator
	// similarly to a java classpath.
	private String fontPath = MT4jSettings.DEFAULT_FONT_PATH;
    // Maps font names to the files that contains them.
	// If the file path is null, it's a system font.
	private Map<String, String> availableFonts;
	// Reverse mapping of availableFonts, but omitting mappings for which the file path is null
	private Map<String, String> availableFontsReverse;
	// Maps font resource names to the temp files they've been stored in.
	private Map<String, String> fontResourcesToFiles;
	
	private static final int CACHE_MAX_SIZE = 10;
		
	/**
	 * Instantiates a new font manager.
	 */
	private EnhancedFontManager(){
		
		fonts = new HashMap<String, List<IFont>> ();
		suffixToFactory = new HashMap<String, IEnhancedFontFactory>();
		fontResourcesToFiles = new HashMap<String, String>();
		
		//Register default font factories
		registerFontFactory(".ttf", new EnhancedTTFontFactory());
		this.registerFontFactory(".svg", new EnhancedSvgFontFactory());
	    
		EnhancedBitmapFontFactory bitmapFontFactory = new EnhancedBitmapFontFactory();

		this.registerFontFactory("", bitmapFontFactory);
	    this.registerFontFactory(".vlw", bitmapFontFactory);
	    this.registerFontFactory(".otf", bitmapFontFactory);
	    
	    this.registerFontFactory(".bold", bitmapFontFactory);
	    this.registerFontFactory(".bolditalic", bitmapFontFactory);
	    this.registerFontFactory(".italic", bitmapFontFactory);
	    this.registerFontFactory(".plain", bitmapFontFactory);
	    
	       // Start with the universal default.
        String fontPath = MT4jSettings.DEFAULT_FONT_PATH;
        switch (PApplet.platform) {
        case PApplet.WINDOWS:
            // 99% of the time in C:\Windows\Fonts
            String drives = "CDEABFGH";
            int len = drives.length();
            for (int i = 0; i < len; i++) {
                String fontDir = drives.substring(i, i + 1)
                        + ":\\Windows\\Fonts";
                if (new File(fontDir).isDirectory()) {
                    fontPath += File.pathSeparator + fontDir;
                    break;
                }
            }
            break;
        case PApplet.MACOSX:
            // TODO:
            break;
        case PApplet.LINUX:
            // TODO:
            break;
        case PApplet.OTHER:
            // TODO:
            break;
        }

        this.fontPath = fontPath;
	}
	
	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 * 
	 * this VectorFontManager, use <code>createFont</code> to create a font with it
	 */
	public static EnhancedFontManager getInstance(){ 
		if (fontManager == null){
			fontManager = new EnhancedFontManager();
			return fontManager;
		}else{
			return fontManager;
		}
	}
	
	public void setFontPath(String fontPath) {
	  if (fontPath == null) {
	    throw new NullPointerException();
	  }
	  if (!fontPath.equals(this.fontPath)) {
	    this.fontPath = fontPath;
	    availableFonts = null;
	  }
	}
	
	public String getFontPath() {
	  return fontPath;
	}
	
	public String[] fontPaths() {
	  StringTokenizer tokenizer = new StringTokenizer(File.pathSeparator);
	  List<String> pathList = new ArrayList<String>(tokenizer.countTokens());
	  while(tokenizer.hasMoreTokens()) {
	    String path = tokenizer.nextToken().trim();
	    if (path.length() > 0) {
	      pathList.add(path);
	    }
	  }
	  return pathList.toArray(new String[pathList.size()]);
	}
	
	private void checkAvailableFontsCurrent() {
		if (availableFonts == null) {
			loadAvailableFonts();
		}	
	}
	
	private void loadAvailableFonts() {
		
		Map<String, String> availableFonts = new TreeMap<String, String> ();
		Map<String, String> availableFontsReverse = new TreeMap<String, String> ();
		
		String[] fps = fontPaths();
		
		// For filtering out files without extensions that map to factories.
		final Set<String> fileExtensions = new HashSet<String>(suffixToFactory.keySet());
		
		for (int i=0; i<fps.length; i++) {
			
			File dir = new File(fps[i]);
			
			if (dir.isDirectory()) {
				
				File[] files = dir.listFiles(new FileFilter() {
					public boolean accept(File f) {
						if (f.isFile()) {
							String fn = f.getName();
							int n = fn.lastIndexOf('.');
							String ext = (n >= 0) ? fn.substring(n) : "";
							return fileExtensions.contains(ext);
						}
						return false;
					}
				});
				
				for (File file: files) {
					String fn = file.getName();
					int n = fn.lastIndexOf('.');
					String ext = (n >= 0) ? fn.substring(n) : "";
					IEnhancedFontFactory factory = suffixToFactory.get(ext);
					if (factory != null) { 
						String filePath = file.getAbsolutePath();
						String fontName = factory.extractFontName(filePath);
						if (fontName != null && fontName.length() > 0) {								
							availableFonts.put(fontName, filePath);
							availableFontsReverse.put(filePath, fontName);
						}
					}
				} 	
			}
		} 
		
		// Several system fonts may not map to any of the files found,
		// yet they are still available.		
		String[] allFontNames = PFont.list();
		for (String fn: allFontNames) {
			if (!availableFonts.containsKey(fn)) {
				availableFonts.put(fn, null);
			}
		}
		
		/* Just prints out all the info.
		Iterator<String> it = availableFonts.keySet().iterator();
		while(it.hasNext()) {
			String fn = it.next();
			String filename = availableFonts.get(fn);
			if (filename == null) filename = "[SYSTEM]";
			System.out.println("\t\t... " + fn + " ==> " + filename);
		}
		*/
		
		this.availableFonts = availableFonts;
		this.availableFontsReverse = availableFontsReverse;
	}
	
	public synchronized String[] availableFonts() {
		checkAvailableFontsCurrent();
		Set<String> keys = availableFonts.keySet();
		return keys.toArray(new String[keys.size()]);
	}
	
	public synchronized boolean isFontAvailable(String fontName) {
		checkAvailableFontsCurrent();
		return availableFonts.containsKey(fontName);
	}

	/**
	 * Gets the default font.
	 *
	 * @param app the app
	 * @return the default font
	 */
	public IFont getDefaultFont(PApplet app){
		return createFont(app, 
		    FontManager.DEFAULT_FONT, 
		    FontManager.DEFAULT_FONT_SIZE, 
		    new MTColor(FontManager.DEFAULT_FONT_STROKE_COLOR), 
		    FontManager.DEFAULT_FONT_ANTIALIASING);
	}
	
	
	/**
	 * Creates the font.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param antiAliased the anti aliased
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, boolean antiAliased){
		return createFont(pa, fontFileName, fontSize, new MTColor(
		    FontManager.DEFAULT_FONT_FILL_COLOR), antiAliased);
	}
	
	
	/**
	 * Loads and returns a font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 * <br>Example: "IFont font = FontManager.createFont(papplet, "Pakenham.svg", 100);"
	 * 
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * 
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize){
		return createFont(pa, fontFileName, fontSize, new MTColor(
		    FontManager.DEFAULT_FONT_FILL_COLOR));
	}
	
	/**
	 * Loads and returns a vector font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 * 
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return the i font
	 * @deprecated from now on, only a single font color is supported for conformity across factories
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, strokeColor, true);
	}
	
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor color) {
		return this.createFont(pa, fontFileName, fontSize, color, true);
	}
	

	/**
	 * Creates the font.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @return the i font
	 * @deprecated from now on, only a single font color is supported for conformity across factories 
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor fillColor, MTColor strokeColor, boolean antiAliased) {
		return this.createFont(pa, fontFileName, fontSize, fillColor, antiAliased);
	}
	
	public IFont createFontByName(PApplet pa, final String fontName, 
			int fontSize, MTColor color) {
		return createFontByName(pa, fontName, fontSize, color, true);
	}

	public IFont createFontByName(PApplet pa, final String fontName, 
			int fontSize, MTColor color, 
			boolean antiAliased) {

		checkAvailableFontsCurrent();
		
		String fontFileName = this.availableFonts.get(fontName);
		if (fontFileName == null || fontFileName.length() == 0) {
			fontFileName = fontName;
		}
		
		return createFont(pa, fontFileName, fontSize, color, antiAliased);
	}
	
	/**
	 * Loads and returns a vector font from a file.
	 * <br>The file has to be located in the ./data/ directory of the program.
	 *
	 * @param pa the pa
	 * @param fontFileName the font file name
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * @param antiAliased the anti aliased
	 * @return the i font
	 */
	public IFont createFont(PApplet pa, String fontFileName, int fontSize, MTColor color, boolean antiAliased) {
		
		checkAvailableFontsCurrent();
		
		// Try to find a file that exists in one of the font directories with that file name.
		String fontAbsolutePath = fontFileName;
		String[] fontPaths = fontPaths();
		for (String fontPath: fontPaths) {
			File f = new File(new File(fontPath), fontFileName);
			if (f.exists() && f.isFile()) {
				fontAbsolutePath = f.getAbsolutePath();
				break;
			}
		}
		
		return createFontFromFile(pa, fontAbsolutePath, fontSize, color, antiAliased);
	}
	
	public IFont createFontFromResource(PApplet pa, String fontResourceName, int fontSize,
			MTColor color) {
		return createFontFromResource(pa, fontResourceName, fontSize, color, true);
	}

	/**
	 * Loads and returns a font stored as a resource instead of in a file in one of
	 * the font directories.
	 * 
	 * @param pa
	 * @param fontResourceName
	 * @param fontSize
	 * @param color
	 * @param antiAliased
	 * @return
	 */
	public IFont createFontFromResource(PApplet pa, String fontResourceName, int fontSize,
			MTColor color, boolean antiAliased) {
		
		String fontAbsolutePath = fontResourcesToFiles.get(fontResourceName);
		
		if (fontAbsolutePath == null) {

			String resourcePath = MT4jSettings.DEFAULT_FONT_PATH + fontResourceName;
			InputStream in = null;
			OutputStream out = null;
			
			try {
				
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
			
				// Copy it to a temp file, so the normal method can be used.
				//
				if (in != null) {
				
					File tempFile = File.createTempFile("tmpfont", getFontSuffix(fontResourceName));
					tempFile.deleteOnExit();
					
					out = new FileOutputStream(tempFile);
					byte[] buffer = new byte[4096];
					
					int bytesRead = 0;
					while((bytesRead = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}
					out.flush();
					out.close();
					out = null;
					
					in.close();
					in = null;
					
					fontAbsolutePath = tempFile.getAbsolutePath();
					fontResourcesToFiles.put(fontResourceName, fontAbsolutePath);
				}
				
			} catch (IOException ioe) {
				
				logger.error("Error copying resource " + fontResourceName + " to temporary file.");
				
			} finally {
				
				if (in != null) {
					try { in.close(); } catch (IOException ioe2) { /* ignore */ }
				}
				if (out != null) {
					try { out.close(); } catch (IOException ioe2) { /* ignore */ }
				}
			}
		}
		
		return createFontFromFile(pa, fontAbsolutePath, fontSize, color, antiAliased);
	}
	
	private IFont createFontFromFile(PApplet pa,  String fontAbsolutePath, 
			int fontSize, MTColor color, boolean antiAliased) {

		checkAvailableFontsCurrent();
		
		String fontName = availableFontsReverse.get(fontAbsolutePath);
		
		if (fontName == null) {
			// It might be one of the system fonts that doesn't map to a file name.
			File f = new File(fontAbsolutePath);
			
			fontName = f.getName();
			
			if (!f.isFile()) {
			  // Work-around for a bug in BitmapFontFactoryProxy.
			  fontAbsolutePath = File.separator + fontAbsolutePath;
			}
		}
	
		IFont loadedFont = null;
			
		if (fontName != null) {
			
			//Return cached font if there
			IFont font = this.getCachedFont(fontName, fontSize,	color, antiAliased);
			if (font != null) {
				return font;
			}
	
			try {
			
				String suffix = getFontSuffix(fontAbsolutePath);

				//Check which factory to use for this file type
				IEnhancedFontFactory factoryToUse = getFactoryForFileSuffix(suffix);

				if (factoryToUse != null) {
				
					logger.info("Loading new font \"" + fontName + "\" with factory: " + factoryToUse.getClass().getName());
					logger.info("Font file = " + fontAbsolutePath);
					
					loadedFont = factoryToUse.createFont(pa, fontAbsolutePath, fontSize, color, antiAliased);
					
					// Have to be sure it's not null.
					if (loadedFont != null) {						
						cacheFont(fontName, loadedFont);
					}
					
				} else {
					logger.error("Couldnt find a appropriate font factory for: " + fontName + " Suffix: " + suffix);
				}
		
		} catch (Exception e) {
			logger.error("Error while trying to create the font: " + fontName);
			e.printStackTrace();
		}
	}
	
	return (loadedFont);
}
	
	private String getFontSuffix(String fontFileName){
		int indexOfPoint = fontFileName.lastIndexOf(".");
		String suffix;
		if (indexOfPoint != -1){
			suffix = fontFileName.substring(indexOfPoint, fontFileName.length());
			suffix = suffix.toLowerCase();
		}else{
			suffix = "";
		}
		return suffix;
	}
	
	/**
	 * Register a new fontfactory for a file type.
	 * 
	 * @param enhancedFontFactory the factory
	 * @param fileSuffix the file suffix to use with that factory. ".ttf" for example.
	 */
	public void registerFontFactory(String fileSuffix, IEnhancedFontFactory enhancedFontFactory){
		fileSuffix = fileSuffix.toLowerCase();
		this.suffixToFactory.put(fileSuffix, enhancedFontFactory);
	}
	
	/**
	 * Unregister a fontfactory for a file type.
	 * 
	 * @param factory the factory
	 */
	public void unregisterFontFactory(IEnhancedFontFactory factory){
		Set<String> suffixesInHashMap = this.suffixToFactory.keySet();
		for (Iterator<String> iter = suffixesInHashMap.iterator(); iter.hasNext();) {
			String suffix = (String) iter.next();
			if (this.getFactoryForFileSuffix(suffix).equals(factory)){
				this.suffixToFactory.remove(suffix);
			}
		}
	}
	
	
	/**
	 * Gets the registered factories.
	 * @return the registered factories
	 */
	public IEnhancedFontFactory[] getRegisteredFactories(){
		Collection<IEnhancedFontFactory> factoryCollection = this.suffixToFactory.values();
		return factoryCollection.toArray(new IEnhancedFontFactory[factoryCollection.size()]);
	}
	
	
	/**
	 * Gets the factory for file suffix.
	 * @param suffix the suffix
	 * @return the factory for file suffix
	 */
	public IEnhancedFontFactory getFactoryForFileSuffix(String suffix){
		return this.suffixToFactory.get(suffix);
	}
		
	/**
	 * Gets the cached font.
	 * 
	 * @param fontAbsoultePath the font absoulte path
	 * @param fontSize the font size
	 * @param fillColor the fill color
	 * @param strokeColor the stroke color
	 * 
	 * @return the cached font
	 */
	public IFont getCachedFont(String fontName, int fontSize, MTColor fillColor, boolean antiAliased) {
		// Get a list of fonts registered under the given name.
		List<IFont> fontList = fonts.get(fontName);
		if (fontList != null) {
			for (IFont font : fontList){
				// Don't need to get the font name from the font object, since all fonts in
				// the list have the specified fontName.
				if (font.getOriginalFontSize() == fontSize &&
					font.getFillColor().equals(fillColor) &&
					font.isAntiAliased() == antiAliased) {
					logger.info("Using cached font: " + fontName + " Fontsize: " + Math.round(fontSize) +
						" FillColor: " + fillColor);
				    return font;
				}
			}
		}
		return null;
	}
	
	private void cacheFont(String fontName, IFont font) {
		List<IFont> fontList = fonts.get(fontName);
		if (fontList == null) {
			fontList = new ArrayList<IFont> ();
			fonts.put(fontName, fontList);
		}
		fontList.add(font);	
		checkFontCacheSize();
	}
	
	private void checkFontCacheSize() {
		int totalSz = 0;
		Iterator<List<IFont>> it = fonts.values().iterator();
		while(it.hasNext()) {
			totalSz += it.next().size();
		}
		if (totalSz > CACHE_MAX_SIZE && totalSz > 0) {
			it = fonts.values().iterator();
			while(it.hasNext()) {
				List<IFont> fontList = it.next();
				if (fontList.size() > 0) {
					fontList.remove(0);
					totalSz--;
					if (totalSz == CACHE_MAX_SIZE || totalSz == 0) {
						return;
					}
 				}
			}
		}
	}

	/**
	 * Removes the font from the cache.
	 * <br><b>NOTE:</b> does not destroy the font! To cleanly destroy a font 
	 * AND remove it from the fontmanager cache call <code>font.destroy()</code>.
	 *
	 * @param font the font
	 * @return true, if successful
	 */
	public boolean removeFromCache(IFont font) {
		
		Iterator<List<IFont>> it = fonts.values().iterator();
		while(it.hasNext()) {
			List<IFont> list = it.next();
			int n = list.indexOf(font);
			if (n >= 0) {
				list.remove(n);
				return true;
			}
		}
		
		return false;
	}		
}
