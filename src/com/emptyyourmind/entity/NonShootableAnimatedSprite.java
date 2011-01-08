package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class NonShootableAnimatedSprite extends AnimatedSprite
{
	private IPositionChangedListener iPositionChangedListener;
	
	public NonShootableAnimatedSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion, ILayer layer)
	{
		super(pX, pY, pTiledTextureRegion);
		layer.addEntity(this);
	}
	
	@Override
	protected void onPositionChanged()
	{
		super.onPositionChanged();
		if(iPositionChangedListener != null)
		{
			iPositionChangedListener.onPositionChanged();
		}
	}

	public void setiPositionChangedListener(IPositionChangedListener iPositionChangedListener)
	{
		this.iPositionChangedListener = iPositionChangedListener;
	}

}
