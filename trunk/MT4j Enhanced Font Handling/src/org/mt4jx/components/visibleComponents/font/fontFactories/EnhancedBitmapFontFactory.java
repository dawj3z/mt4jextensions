 /**
 * This material was prepared as an account of work sponsored by an agency of the United States Government.<br>
 * Neither the United States Government nor the United States Department of Energy, nor any of their employees,<br> 
 * nor any of their contractors, subcontractors or their employees, makes any warranty, express or implied, or<br>
 * assumes any legal liability or responsibility for the accuracy, completeness, or usefulness or any information,<br> 
 * apparatus, product, or process disclosed, or represents that its use would not infringe privately owned rights.
 */
package org.mt4jx.components.visibleComponents.font.fontFactories;

import org.mt4j.components.visibleComponents.font.fontFactories.BitmapFontFactory;

/**
 * <p>
 * A factory for creating BitmapFont objects.
 * </p>
 * 
 * @author R.Scarberry
 */
public class EnhancedBitmapFontFactory extends BitmapFontFactory 
  implements IEnhancedFontFactory {
	
    /**
     * Not yet implemented.  This method simply returns null.
     */
	public String extractFontName(String fontFileName) {
	    //TODO: Find out how to extract font names from .vlw and .otf files.
		return null;
	}	
}
