package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class ShootableSprite extends NonShootableSprite implements IShootable
{
	private IShootable iShootable;

	public ShootableSprite(float pX, float pY, TextureRegion pTextureRegion, ILayer layer)
	{
		super(pX, pY, pTextureRegion, layer);
	}

	public void setiShootable(IShootable iShootable)
	{
		this.iShootable = iShootable;
	}

	public void shoot()
	{
		iShootable.shoot();
	}

}
