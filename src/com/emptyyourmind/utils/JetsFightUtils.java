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
		final boolean completelyInsideRec = (centerX >= xTop && centerX <= xBottom && centerY >= yLeft && centerY <= yRight);
		final boolean intersectTopLeft = Math.abs(centerX - xTop) < radius && Math.abs(centerY - yLeft) < radius;
		final boolean intersectTopRight = Math.abs(centerX - xBottom) < radius &&  Math.abs(centerY - yLeft) < radius;
		final boolean intersectBottomLeft = Math.abs(centerX - xTop) < radius && Math.abs(centerY - yRight) < radius;
		final boolean intersectBottomRight = Math.abs(centerX - xBottom) < radius && Math.abs(centerY - yRight) < radius;
		
		return completelyInsideRec || intersectTopLeft || intersectTopRight || intersectBottomLeft || intersectBottomRight;
	}
}
