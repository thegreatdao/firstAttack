package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class ShootableAnimatedSprite extends NonShootableAnimatedSprite implements IShootable
{
	private IShootable iShootable;

	public ShootableAnimatedSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion, ILayer layer)
	{
		super(pX, pY, pTiledTextureRegion, layer);
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
