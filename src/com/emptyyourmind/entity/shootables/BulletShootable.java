package com.emptyyourmind.entity.shootables;

import org.anddev.andengine.entity.shape.Shape;

import com.emptyyourmind.entity.IShootable;

public class BulletShootable implements IShootable
{
	private float xVelocity;
	private float yVelocity;
	private Shape shotFrom;
	
	public BulletShootable(Shape attachTo, float xVelocity, float yVelocity)
	{
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.shotFrom = attachTo;
	}

	@Override
	public void shoot()
	{
		shotFrom.setVelocity(xVelocity, yVelocity);
	}

}
