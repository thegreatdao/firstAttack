package com.emptyyourmind.utils;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.Shape;

public class JetsFightUtils
{
	public static boolean isInCamera(Camera camera, Shape shape, float cameraHalfWidth, float cameraHalfHeight)
	{
		float centerX = camera.getCenterX();
		float centerY = camera.getCenterY();
		if ((shape.getX() >= (centerX - cameraHalfWidth - shape .getWidthScaled())) && (shape.getX() <= (centerX + cameraHalfWidth)) && (shape.getY() >= (centerY - cameraHalfHeight - shape.getHeightScaled())) && (shape.getY() <= (centerY + cameraHalfHeight)))
		{
			return true;
		}
		return false;
	}
	
	public static boolean ciricleCollidesWithRectangle(final float centerX, final float centerY, final float radius, final float xTop, final float yLeft, final float xBottom, final float yRight)
	{
		final float distance = Math.abs(centerX - xTop);
		float distance2 = Math.abs(centerX - xBottom);
		float distance3 = Math.abs(centerY - yLeft);
		float distance4 = Math.abs(centerY - yRight);
		return  distance <= radius || distance2 <= radius || 
				distance3 <= radius || distance4 <= radius || (centerX >= xTop && centerX <= xBottom && centerY >= yLeft && centerY <= yRight);
	}
}
