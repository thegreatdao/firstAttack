package com.emptyyourmind.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class CustomizedAnimatedSprite extends AnimatedSprite
{
	private IPositionChangedListener iPositionChangedListener;
	
	public CustomizedAnimatedSprite(int pX, int pY, TiledTextureRegion pTiledTextureRegion, IPositionChangedListener iPositionChangedListener)
	{
		super(pX, pY, pTiledTextureRegion);
		this.iPositionChangedListener = iPositionChangedListener;
	}
	
	@Override
	protected void onPositionChanged()
	{
		super.onPositionChanged();
		if(iPositionChangedListener != null)
		{
			iPositionChangedListener.onPositionChanged(mX, mY);
		}
	}

}
