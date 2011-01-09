package com.emptyyourmind.entity.shootables;

import org.anddev.andengine.entity.shape.Shape;

import com.emptyyourmind.entity.IShootable;

public class BulletShootable implements IShootable
{
	private float xVelocity;
	private float yVelocity;
	private Shape shootFrom;
	
	public BulletShootable(Shape shootFrom, float xVelocity, float yVelocity)
	{
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.shootFrom = shootFrom;
	}

	@Override
	public void shoot()
	{
		shootFrom.setVelocity(xVelocity, yVelocity);
	}

}
