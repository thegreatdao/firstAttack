package com.emptyyourmind.entity;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.emptyyourmind.utils.JetsFightUtils;

public class BasePositionChangedListener implements IPositionChangedListener
{
	private Shape shape;
	private ILayer layer;
	private Camera camera;
	private float cameraHalfWidth;
	private float cameraHalfHeight;
	private BaseGameActivity baseGameActivity;

	public BasePositionChangedListener(Shape shape, ILayer layer,
			Camera camera, float cameraHalfWidth, float cameraHalfHeight, BaseGameActivity baseGameActivity)
	{
		this.shape = shape;
		this.layer = layer;
		this.camera = camera;
		this.cameraHalfWidth = cameraHalfWidth;
		this.cameraHalfHeight = cameraHalfHeight;
		this.baseGameActivity = baseGameActivity;
	}

	@Override
	public void onPositionChanged()
	{
		if(!JetsFightUtils.isInCamera(camera, shape, cameraHalfWidth, cameraHalfHeight))
		{
			baseGameActivity.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					layer.removeEntity(shape);					
				}
			});
		}
	}
}
