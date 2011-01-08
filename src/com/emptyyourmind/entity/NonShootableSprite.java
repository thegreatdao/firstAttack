package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class NonShootableSprite extends Sprite
{
	private IPositionChangedListener iPositionChangedListener;
	
	public NonShootableSprite(float pX, float pY, TextureRegion pTextureRegion, ILayer layer)
	{
		super(pX, pY, pTextureRegion);
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
