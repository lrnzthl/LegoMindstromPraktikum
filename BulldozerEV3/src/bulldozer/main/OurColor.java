package bulldozer.main;

import java.awt.Color;

public class OurColor {

	private Color pColor;
	
	public OurColor(float red, float green, float blue) {
		
		pColor = new Color(red, green, blue);
		
	}
	
	public OurColor ( float[] RGB) {
		float red = RGB[0];
		float green = RGB[1];
		float blue = RGB[2];
		
		pColor = new Color (red, green, blue);
		
	}

	public void setpColor(Color c) {
		this.pColor = c;
		
	}
	
	public Color getpColor() {
		return pColor;
	}

	public int getRed() {
		return pColor.getRed();
	}
	
	public int getGreen() {
		return pColor.getGreen();
	}

	public int getBlue() {
		return pColor.getBlue();
	}
	
	public float getIntensity() {
		float intensity = (pColor.getBlue() + pColor.getGreen() + pColor.getRed())/3;
		return intensity;
	}
	
}
