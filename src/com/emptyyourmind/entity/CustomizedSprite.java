package com.emptyyourmind.entity;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class CustomizedSprite extends Sprite
{
	private IPositionChangedListener iPositionChangedListener;

	public CustomizedSprite(float pX, float pY, TextureRegion pTextureRegion)
	{
		super(pX, pY, pTextureRegion);
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

	public IPositionChangedListener getiPositionChangedListener()
	{
		return iPositionChangedListener;
	}

	public void setiPositionChangedListener(
			IPositionChangedListener iPositionChangedListener)
	{
		this.iPositionChangedListener = iPositionChangedListener;
	}
	
}
