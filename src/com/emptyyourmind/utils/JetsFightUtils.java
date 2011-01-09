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
}
