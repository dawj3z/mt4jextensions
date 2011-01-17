package org.mt4j.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class Mp3Player {
	private static boolean isEnabled = true;
	public static void setEnabled(boolean enabled){
		isEnabled = enabled;
	}
	
	public static void play(final String audioFilePath){
		if(isEnabled){
			try {
				final AdvancedPlayer p = new AdvancedPlayer(new FileInputStream(new File(audioFilePath)));
				Thread t = new Thread(){
					@Override
					public void run() {
						try {
							p.play();
						} catch (JavaLayerException e) {
							e.printStackTrace();
						}
					}
				};
				t.setPriority(Thread.MIN_PRIORITY);
				t.start();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
		}
	}
}
