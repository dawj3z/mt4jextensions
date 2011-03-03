package org.mt4jx.examples;

import org.mt4j.MTApplication;

public class StartEnhancedFontSelectScene extends MTApplication {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void startUp() {
    addScene(new EnhancedFontSelectScene(this, "Font Selection Test"));
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    initialize();
  }

}
