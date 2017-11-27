package bulldozer.main;

import java.awt.Color;

public class CColor {

	private float r;
	private float g;
	private float b;

	private float tolerance = 1; //for comparing 2 colors

	public CColor(float red, float green, float blue) {
		this.r = red;
		this.g = green;
		this.b = blue;
	}
	
	public CColor(float[] RGB) {
		this.r = RGB[0];
		this.g = RGB[1];
		this.b = RGB[2];
	}

	public void setColor(Color c) {
		this.r = c.getRed() / 255.f;
		this.g = c.getGreen() / 255.f;
		this.b = c.getBlue() / 255.f;
	}
	
	public Color getpColor() {
		return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
	}

	public float getRed() {
		return this.r;
	}
	
	public float getGreen() {
		return this.g;
	}

	public float getBlue() {
		return this.b;
	}
	
	public float getIntensity() {
		return (r+g+b)/3.f;
	}
	
	@Override
	public boolean equals(Object obj) {


	    if (obj == null) {
	        return false;
	    }
	    if (!CColor.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }



        final CColor other = (CColor) obj;


	    if (this.r != other.getRed() || this.g != other.getGreen() || this.b != other.getBlue()) {
	        return false;
	    }
	    return true;


	}


    /**
     * equals with tolerance!
     * @param obj
     * @return if the 2 colors are the same
     */
    public boolean equalsTolerance(Object obj) {


        if (obj == null) {
            return false;
        }
        if (!CColor.class.isAssignableFrom(obj.getClass())) {
            return false;
        }



        final CColor other = (CColor) obj;


        float lowerMultiply = 1.f - tolerance;
        float upperMultiply = 1.f + tolerance;


        CColor lowerColor = new CColor(this.getRed()*lowerMultiply,
                this.getGreen()*lowerMultiply,
                this.getBlue()*lowerMultiply);
        CColor upperColor = new CColor(this.getRed()*upperMultiply,
                this.getGreen()*upperMultiply,
                this.getBlue()*upperMultiply);

        return other.isGreaterThan(lowerColor) && other.isLessThan(upperColor);

    }
	
	public boolean isGreaterThan(CColor toCompare) {
		boolean returnValue = false;
		if(this.r > toCompare.getRed() && 
		   this.g > toCompare.getGreen() && 
		   this.b > toCompare.getBlue()){
			returnValue = true;
		}
	    return returnValue;
	}

	public boolean isLessThan(CColor toCompare) {
		boolean returnValue = false;
		if(this.r < toCompare.getRed() && 
		   this.g < toCompare.getGreen() && 
		   this.b < toCompare.getBlue()){
			returnValue = true;
		}
	    return returnValue;
	}

	public String toString(){
		return  "color r:"+r+", g:"+g+", b:"+b+", intens:"+getIntensity();
	}
}
